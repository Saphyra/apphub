package com.github.saphyra.apphub.service.feature.elite_base.processor.migration.migrator;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.Item;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemIdAssembler;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemLocationType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_FLEET_CARRIER_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_ITEM_NAME;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_LOCATION_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_MARKET_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_STATION_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_ITEM_EQUIPMENT;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_ITEM_EQUIPMENT_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.migration.MigratorConstants.MAX_BATCH_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class ItemEquipmentMigrator {
    //Ensure migration runs after station is migrated
    @SuppressWarnings("unused")
    private final StationMigrator stationMigrator;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ErrorReporterService errorReporterService;
    private final UuidConverter uuidConverter;
    private final ItemIdAssembler itemIdAssembler;

    @PostConstruct
    void migrate() {
        log.info("Migrating ItemEquipment to V2...");

        int totalCount = 0;
        List<BiWrapper<BiWrapper<String, String>, Map<String, Object>>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to ItemEquipment V2", totalCount);
    }

    private void delete(List<BiWrapper<BiWrapper<String, String>, Map<String, Object>>> batch) {
        if (batch.isEmpty()) {
            return;
        }

        List<Map<String, String>> ids = batch.stream()
            .map(BiWrapper::getEntity1)
            .map(bw -> Map.of(
                COLUMN_ITEM_NAME, bw.getEntity1(),
                COLUMN_EXTERNAL_REFERENCE, bw.getEntity2()
            ))
            .toList();

        String sql = "DELETE FROM %s.%s WHERE %s = :%s AND %s = :%s".formatted(
            SCHEMA,
            TABLE_ITEM_EQUIPMENT,
            COLUMN_ITEM_NAME,
            COLUMN_ITEM_NAME,
            COLUMN_EXTERNAL_REFERENCE,
            COLUMN_EXTERNAL_REFERENCE
        );

        try {
            jdbcTemplate.batchUpdate(
                sql,
                ids.toArray(Map[]::new)
            );
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private void save(List<BiWrapper<BiWrapper<String, String>, Map<String, Object>>> batch) {
        if (batch.isEmpty()) {
            return;
        }

        List<Map<String, Object>> entities = batch.stream()
            .map(BiWrapper::getEntity2)
            .toList();

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_ITEM_EQUIPMENT_V2), entities.getFirst().keySet()).build();
        try {
            jdbcTemplate.batchUpdate(
                sql,
                entities.toArray(Map[]::new)
            );
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private List<BiWrapper<BiWrapper<String, String>, Map<String, Object>>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_ITEM_NAME, COLUMN_EXTERNAL_REFERENCE, COLUMN_LOCATION_TYPE, COLUMN_MARKET_ID, COLUMN_STAR_SYSTEM_ID)
            .from(new QualifiedTable(SCHEMA, TABLE_ITEM_EQUIPMENT))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<BiWrapper<BiWrapper<String, String>, Map<String, Object>>> result = new ArrayList<>();
                while (rs.next()) {
                    String itemName = rs.getString(COLUMN_ITEM_NAME);
                    String externalReference = rs.getString(COLUMN_EXTERNAL_REFERENCE);
                    String locationType = rs.getString(COLUMN_LOCATION_TYPE);

                    Item item = new Item() {
                        @Override
                        public String getItemName() {
                            return itemName;
                        }

                        @Override
                        public UUID getExternalReference() {
                            return uuidConverter.convertEntity(externalReference);
                        }

                        @Override
                        public ItemLocationType getLocationType() {
                            return ItemLocationType.valueOf(locationType);
                        }
                    };

                    Map<String, Object> map = CollectionUtils.toMap(
                        new BiWrapper<>(COLUMN_ID, itemIdAssembler.assembleId(item)),
                        new BiWrapper<>(COLUMN_ITEM_NAME, itemName),
                        new BiWrapper<>(COLUMN_STATION_ID, uuidConverter.convertDomain(item.getStationId())),
                        new BiWrapper<>(COLUMN_FLEET_CARRIER_ID, uuidConverter.convertDomain(item.getFleetCarrierId())),
                        new BiWrapper<>(COLUMN_MARKET_ID, rs.getBigDecimal(COLUMN_MARKET_ID).longValue()),
                        new BiWrapper<>(COLUMN_STAR_SYSTEM_ID, rs.getString(COLUMN_STAR_SYSTEM_ID))
                    );

                    result.add(
                        new BiWrapper<>(
                            new BiWrapper<>(
                                rs.getString(COLUMN_ITEM_NAME),
                                rs.getString(COLUMN_EXTERNAL_REFERENCE)
                            ),
                            map
                        )
                    );
                }
                return result;
            }
        );
    }
}
