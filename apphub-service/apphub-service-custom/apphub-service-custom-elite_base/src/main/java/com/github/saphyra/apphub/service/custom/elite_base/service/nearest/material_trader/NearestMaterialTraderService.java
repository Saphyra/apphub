package com.github.saphyra.apphub.service.custom.elite_base.service.nearest.material_trader;

import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import com.github.saphyra.apphub.api.custom.elite_base.model.NearestMaterialTraderResponse;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.util.Utils;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.Condition;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.ConditionGroup;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.DefaultColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.DefaultSegmentProvider;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.Equation;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.InCondition;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.IsNullEquation;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.ListValue;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.NamedColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.OrderType;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.PowerSegment;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedTable;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SqlBuilder;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SquareRootSegment;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SubtractSegment;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SumSegment;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.TrueCondition;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.WrappedValue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class NearestMaterialTraderService {
    private static final Map<MaterialType, List<EconomyEnum>> MATERIAL_TYPE_MAPPING = Map.of(
        MaterialType.ANY, List.of(EconomyEnum.HIGH_TECH, EconomyEnum.INDUSTRIAL, EconomyEnum.EXTRACTION, EconomyEnum.REFINERY),
        MaterialType.RAW, List.of(EconomyEnum.EXTRACTION, EconomyEnum.REFINERY),
        MaterialType.MANUFACTURED, List.of(EconomyEnum.INDUSTRIAL),
        MaterialType.ENCODED, List.of(EconomyEnum.HIGH_TECH)
    );

    private final StarSystemDao starSystemDao;
    private final JdbcTemplate jdbcTemplate;
    private final UuidConverter uuidConverter;
    private final EliteBaseProperties properties;

    @SneakyThrows
    public List<NearestMaterialTraderResponse> getNearestMaterialTraders(UUID starId, MaterialType materialType, Integer page) {
        StarSystem starSystem = starSystemDao.findByIdValidated(starId);

        String sql = SqlBuilder.select()
            .column(new NamedColumn(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_ID), COLUMN_STAR_SYSTEM_ID))
            .columns(COLUMN_STAR_NAME, COLUMN_X_POS, COLUMN_Y_POS, COLUMN_Z_POS, COLUMN_STATION_NAME, COLUMN_DISTANCE_FROM_STAR, COLUMN_ECONOMY, COLUMN_MATERIAL_TYPE, COLUMN_VERIFIED)
            .column(new NamedColumn(new QualifiedColumn(TABLE_STATION, COLUMN_ID), COLUMN_STATION_ID))
            .column(new NamedColumn(
                new SquareRootSegment(
                    new SumSegment(
                        new PowerSegment(new SubtractSegment(new DefaultColumn(COLUMN_X_POS), new DefaultSegmentProvider(starSystem.getPosition().getX())), 2),
                        new PowerSegment(new SubtractSegment(new DefaultColumn(COLUMN_Y_POS), new DefaultSegmentProvider(starSystem.getPosition().getY())), 2),
                        new PowerSegment(new SubtractSegment(new DefaultColumn(COLUMN_Z_POS), new DefaultSegmentProvider(starSystem.getPosition().getZ())), 2)
                    )
                ),
                COLUMN_DISTANCE_FROM_REFERENCE
            ))
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM))
            .innerJoin(new QualifiedTable(SCHEMA, TABLE_STATION), new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_ID), new QualifiedColumn(TABLE_STATION, COLUMN_STAR_SYSTEM_ID))
            .leftJoin(new QualifiedTable(SCHEMA, TABLE_BODY), new QualifiedColumn(TABLE_STATION, COLUMN_BODY_ID), new QualifiedColumn(TABLE_BODY, COLUMN_ID))
            .leftJoin(new QualifiedTable(SCHEMA, TABLE_MATERIAL_TRADER_OVERRIDE), new QualifiedColumn(TABLE_STATION, COLUMN_ID), new QualifiedColumn(TABLE_MATERIAL_TRADER_OVERRIDE, COLUMN_STATION_ID))
            .condition(new InCondition(
                new QualifiedColumn(TABLE_STATION, COLUMN_ID),
                SqlBuilder.select()
                    .column(COLUMN_STATION_ID)
                    .from(new QualifiedTable(SCHEMA, TABLE_STATION_SERVICE))
                    .condition(new Equation(new DefaultColumn(COLUMN_SERVICE), new WrappedValue(StationServiceEnum.MATERIAL_TRADER)))
            ))
            .and()
            .condition(getMaterialTypeCondition(materialType))
            .orderBy(new DefaultColumn(COLUMN_DISTANCE_FROM_REFERENCE), OrderType.ASC)
            .limit(properties.getSearchPageSize())
            .offset(page * properties.getSearchPageSize())
            .build();

        log.info(sql);

        return jdbcTemplate.query(sql, rs -> {
            List<NearestMaterialTraderResponse> result = new ArrayList<>();

            while (rs.next()) {
                EconomyEnum economy = EconomyEnum.fromValue(rs.getString(COLUMN_ECONOMY));
                result.add(NearestMaterialTraderResponse.builder()
                    .starId(uuidConverter.convertEntity(rs.getString(COLUMN_STAR_SYSTEM_ID)))
                    .starName(rs.getString(COLUMN_STAR_NAME))
                    .stationId(uuidConverter.convertEntity(rs.getString(COLUMN_STATION_ID)))
                    .stationName(rs.getString(COLUMN_STATION_NAME))
                    .distanceFromReference(rs.getDouble(COLUMN_DISTANCE_FROM_REFERENCE))
                    .distanceFromStar(Utils.nullIfZero(rs.getDouble(COLUMN_DISTANCE_FROM_STAR)))
                    .materialType(getMaterialType(economy, MaterialType.parse(rs.getString(COLUMN_MATERIAL_TYPE))))
                    .originalMaterialType(getMaterialType(economy))
                    .verifiedMaterialOverride(isNull(rs.getObject(COLUMN_VERIFIED)) ? null : rs.getBoolean(COLUMN_VERIFIED))
                    .build());
            }

            return result;
        });
    }

    private MaterialType getMaterialType(EconomyEnum economy, MaterialType materialType) {
        if (nonNull(materialType)) {
            return materialType;
        }

        return getMaterialType(economy);
    }

    private static MaterialType getMaterialType(EconomyEnum economy) {
        return MATERIAL_TYPE_MAPPING.entrySet()
            .stream()
            .filter(entry -> entry.getKey() != MaterialType.ANY)
            .filter(entry -> entry.getValue().contains(economy))
            .findAny()
            .map(Map.Entry::getKey)
            .orElse(MaterialType.UNKNOWN);
    }

    private static Condition getMaterialTypeCondition(MaterialType materialType) {
        if (materialType == MaterialType.ANY) {
            return new TrueCondition();
        } else {
            return new ConditionGroup()
                .condition(
                    new ConditionGroup()
                        .condition(new IsNullEquation(new DefaultColumn(COLUMN_MATERIAL_TYPE)))
                        .and()
                        .condition(new InCondition(
                            new DefaultColumn(COLUMN_ECONOMY),
                            new ListValue(MATERIAL_TYPE_MAPPING.get(materialType))
                        ))
                )
                .or()
                .condition(new Equation(new DefaultColumn(COLUMN_MATERIAL_TYPE), new WrappedValue(materialType)));
        }
    }
}
