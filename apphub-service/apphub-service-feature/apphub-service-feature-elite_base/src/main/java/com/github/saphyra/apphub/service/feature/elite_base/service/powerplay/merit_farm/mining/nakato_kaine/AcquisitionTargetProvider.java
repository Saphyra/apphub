package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionCoordinate;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionDistanceCalculator;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.column.QualifiedColumn;
import com.github.saphyra.apphub.lib.sql_builder.condition.BetweenCondition;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.value.NumberValue;
import com.github.saphyra.apphub.lib.sql_builder.value.WrappedValue;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_POWERPLAY_STATE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_NAME;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_X_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_Y_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_Z_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_DATA;

@Component
@RequiredArgsConstructor
@Slf4j
class AcquisitionTargetProvider {
    private static final int STRONGHOLD_RANGE = 30;
    private static final Map<PowerplayState, Integer> ACQUISITION_RANGES = Map.of(
        PowerplayState.FORTIFIED, 20,
        PowerplayState.STRONGHOLD, STRONGHOLD_RANGE
    );

    private final JdbcTemplate jdbcTemplate;
    private final UuidConverter uuidConverter;
    private final NDimensionDistanceCalculator distanceCalculator;

    Map<UUID, List<StarSystem>> getAcquisitionTargets(Collection<StarSystem> starSystems, List<StarSystemData> sourceSystemData) {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = Double.MIN_VALUE;
        for (StarSystem starSystem : starSystems) {
            StarSystemPosition position = starSystem.getPosition();
            if (position.getX() < minX) {
                minX = position.getX();
            }
            if (position.getX() > maxX) {
                maxX = position.getX();
            }
            if (position.getY() < minY) {
                minY = position.getX();
            }
            if (position.getY() > maxY) {
                maxY = position.getX();
            }
            if (position.getZ() < minZ) {
                minZ = position.getX();
            }
            if (position.getZ() > maxZ) {
                maxZ = position.getX();
            }
        }

        String sql = SqlBuilder.select()
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM))
            .columns(
                new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_ID),
                new DefaultColumn(COLUMN_STAR_NAME),
                new DefaultColumn(COLUMN_X_POS),
                new DefaultColumn(COLUMN_Y_POS),
                new DefaultColumn(COLUMN_Z_POS)
            )
            .condition(new BetweenCondition(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_X_POS), new NumberValue(minX - STRONGHOLD_RANGE), new NumberValue(maxX + STRONGHOLD_RANGE)))
            .and()
            .condition(new BetweenCondition(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_Y_POS), new NumberValue(minY - STRONGHOLD_RANGE), new NumberValue(maxY + STRONGHOLD_RANGE)))
            .and()
            .condition(new BetweenCondition(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_Z_POS), new NumberValue(minZ - STRONGHOLD_RANGE), new NumberValue(maxZ + STRONGHOLD_RANGE)))
            .and()
            .condition(new Equation(
                new QualifiedColumn(TABLE_STAR_SYSTEM_DATA, COLUMN_POWERPLAY_STATE),
                new WrappedValue(PowerplayState.UNOCCUPIED.name())
            ))
            .innerJoin(
                new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_DATA),
                new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_ID),
                new QualifiedColumn(TABLE_STAR_SYSTEM_DATA, COLUMN_STAR_SYSTEM_ID)
            )
            .build();

        List<StarSystem> unoccupiedSystemsInCube = jdbcTemplate.query(
            sql,
            (row, _) -> StarSystem.builder()
                .id(uuidConverter.convertEntity(row.getString(COLUMN_ID)))
                .starName(row.getString(COLUMN_STAR_NAME))
                .position(StarSystemPosition.builder()
                    .x(row.getDouble(COLUMN_X_POS))
                    .y(row.getDouble(COLUMN_Y_POS))
                    .z(row.getDouble(COLUMN_Z_POS))
                    .build())
                .build()
        );

        return starSystems.stream()
            .collect(Collectors.toMap(StarSystem::getId, starSystem -> getTargetsInRange(starSystem, sourceSystemData, unoccupiedSystemsInCube)));
    }

    private List<StarSystem> getTargetsInRange(StarSystem sourceSystem, List<StarSystemData> sourceSystemData, List<StarSystem> unoccupiedSystemsInCube) {
        Map<UUID, StarSystemData> dataMap = sourceSystemData.stream()
            .collect(Collectors.toMap(StarSystemData::getStarSystemId, starSystemData -> starSystemData));

        int range = ACQUISITION_RANGES.get(dataMap.get(sourceSystem.getId()).getPowerplayState());

        StarSystemPosition sourcePosition = sourceSystem.getPosition();
        NDimensionCoordinate sourceCoordinate = new NDimensionCoordinate(sourcePosition.getX(), sourcePosition.getY(), sourcePosition.getZ());

        return unoccupiedSystemsInCube.stream()
            .filter(starSystem -> {
                StarSystemPosition targetPosition = starSystem.getPosition();
                NDimensionCoordinate targetCoordinate = new NDimensionCoordinate(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
                double distance = distanceCalculator.calculateDistance(sourceCoordinate, targetCoordinate);

                return distance <= range;
            })
            .toList();
    }
}
