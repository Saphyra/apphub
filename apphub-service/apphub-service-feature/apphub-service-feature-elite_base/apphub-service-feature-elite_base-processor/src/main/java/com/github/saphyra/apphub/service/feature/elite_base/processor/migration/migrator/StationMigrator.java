package com.github.saphyra.apphub.service.feature.elite_base.processor.migration.migrator;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.last_update.LastUpdatePartitionCreator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_ALLEGIANCE;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_BODY_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_CONTROLLING_FACTION_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_ECONOMY;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_LAST_UPDATE;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_MARKET_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_OBJECT_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_STATION_NAME;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_LAST_UPDATE_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_STATION;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_STATION_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.migration.MigratorConstants.MAX_BATCH_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class StationMigrator {
    //Ensure migration runs after LastUpdate partition is created
    @SuppressWarnings("unused")
    private final LastUpdatePartitionCreator lastUpdatePartitionCreator;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating Station to V2...");

        int totalCount = 0;
        List<TriWrapper<String, String, Map<String, Object>>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to Station V2", totalCount);
    }

    private void delete(List<TriWrapper<String, String, Map<String, Object>>> batch) {
        String sql = "DELETE FROM %s.%s WHERE %s = :id".formatted(
            SCHEMA,
            TABLE_STATION,
            COLUMN_ID
        );

        try {
            jdbcTemplate.batchUpdate(
                sql,
                batch.stream()
                    .map(TriWrapper::getEntity1)
                    .map(id -> Map.<String, Object>of("id", id))
                    .toArray(Map[]::new)
            );
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }


    private void save(List<TriWrapper<String, String, Map<String, Object>>> batch) {
        List<BiWrapper<String, String>> lastUpdates = batch.stream()
            .map(entry -> new BiWrapper<>(entry.getEntity1(), entry.getEntity2()))
            .toList();
        saveLastUpdates(lastUpdates);

        List<Map<String, Object>> entities = batch.stream()
            .map(TriWrapper::getEntity3)
            .toList();
        saveEntities(entities);
    }

    private void saveEntities(List<Map<String, Object>> entities) {
        if (entities.isEmpty()) {
            return;
        }

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_STATION_V2), entities.getFirst().keySet()).build();
        try {
            jdbcTemplate.batchUpdate(
                sql,
                entities.toArray(Map[]::new)
            );
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private void saveLastUpdates(List<BiWrapper<String, String>> lastUpdates) {
        String sql = SqlBuilder.insert(
                new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE_V2),
                List.of(COLUMN_EXTERNAL_REFERENCE, COLUMN_LAST_UPDATE, COLUMN_OBJECT_TYPE)
            )
            .build();

        try {
            jdbcTemplate.batchUpdate(
                sql,
                lastUpdates.stream()
                    .map(record -> Map.<String, Object>of(
                        COLUMN_EXTERNAL_REFERENCE, record.getEntity1(),
                        COLUMN_LAST_UPDATE, record.getEntity2(),
                        COLUMN_OBJECT_TYPE, ObjectType.STATION.name()
                    ))
                    .toArray(Map[]::new)
            );
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private List<TriWrapper<String, String, Map<String, Object>>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_ID, COLUMN_LAST_UPDATE, COLUMN_STAR_SYSTEM_ID, COLUMN_BODY_ID, COLUMN_STATION_NAME, COLUMN_TYPE, COLUMN_MARKET_ID, COLUMN_ALLEGIANCE, COLUMN_ECONOMY, COLUMN_CONTROLLING_FACTION_ID)
            .from(new QualifiedTable(SCHEMA, TABLE_STATION))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<TriWrapper<String, String, Map<String, Object>>> result = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> map = CollectionUtils.toMap(
                        new BiWrapper<>(COLUMN_ID, rs.getString(COLUMN_ID)),
                        new BiWrapper<>(COLUMN_STAR_SYSTEM_ID, rs.getString(COLUMN_STAR_SYSTEM_ID)),
                        new BiWrapper<>(COLUMN_BODY_ID, rs.getString(COLUMN_BODY_ID)),
                        new BiWrapper<>(COLUMN_STATION_NAME, rs.getString(COLUMN_STATION_NAME)),
                        new BiWrapper<>(COLUMN_TYPE, rs.getString(COLUMN_TYPE)),
                        new BiWrapper<>(COLUMN_MARKET_ID, rs.getBigDecimal(COLUMN_MARKET_ID).longValue()),
                        new BiWrapper<>(COLUMN_ALLEGIANCE, rs.getString(COLUMN_ALLEGIANCE)),
                        new BiWrapper<>(COLUMN_ECONOMY, rs.getString(COLUMN_ECONOMY)),
                        new BiWrapper<>(COLUMN_CONTROLLING_FACTION_ID, rs.getString(COLUMN_CONTROLLING_FACTION_ID))
                    );

                    result.add(new TriWrapper<>(
                        rs.getString(COLUMN_ID),
                        rs.getString(COLUMN_LAST_UPDATE),
                        map
                    ));
                }
                return result;
            }
        );
    }
}
