package com.github.saphyra.apphub.service.feature.elite_base.migration;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.value.WrappedValue;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdatePartitionCreator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_CARRIER_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_DOCKING_ACCESS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_LAST_UPDATE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_MARKET_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_OBJECT_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_FLEET_CARRIER;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_FLEET_CARRIER_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.MAX_BATCH_SIZE;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.THREAD_COUNT;

@RequiredArgsConstructor
@Component
@Slf4j
@Profile("!test")
class FleetCarrierMigrator {
    //Ensure migration runs after partitions are created
    @SuppressWarnings("unused")
    private final LastUpdatePartitionCreator lastUpdatePartitionCreator;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ExecutorServiceBean executorServiceBean;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating FleetCarriers to V2...");

        int totalCount = 0;
        List<TriWrapper<String, String, Map<String, Object>>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to FleetCarrierV2", totalCount);
    }

    private void delete(List<TriWrapper<String, String, Map<String, Object>>> batch) {
        List<String> sqls = batch.stream()
            .map(TriWrapper::getEntity1)
            .map(id -> SqlBuilder.delete()
                .from(new QualifiedTable(SCHEMA, TABLE_FLEET_CARRIER))
                .condition(new Equation(new DefaultColumn(COLUMN_ID), new WrappedValue(id)))
                .build())
            .toList();

        executorServiceBean.processCollectionWithWait(sqls, sql -> jdbcTemplate.update(sql, Map.of()), THREAD_COUNT);
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
        executorServiceBean.processCollectionWithWait(
            entities,
            entity -> {

                String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_FLEET_CARRIER_V2), entity.keySet()).build();
                try {
                    jdbcTemplate.update(sql, entity);
                } catch (Exception e) {
                    errorReporterService.report("Failed to execute SQL: " + sql, e);
                }

                return null;
            },
            THREAD_COUNT
        );
    }

    private void saveLastUpdates(List<BiWrapper<String, String>> lastUpdates) {
        String sql = SqlBuilder.insert(
                new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE_V2),
                List.of(COLUMN_EXTERNAL_REFERENCE, COLUMN_LAST_UPDATE, COLUMN_OBJECT_TYPE)
            )
            .build();

        executorServiceBean.processCollectionWithWait(
            lastUpdates,
            record -> {
                try {
                    jdbcTemplate.update(
                        sql,
                        Map.of(
                            COLUMN_EXTERNAL_REFERENCE, record.getEntity1(),
                            COLUMN_LAST_UPDATE, record.getEntity2(),
                            COLUMN_OBJECT_TYPE, ObjectType.FLEET_CARRIER.name()
                        )
                    );
                } catch (Exception e) {
                    errorReporterService.report("Failed to execute SQL: " + sql, e);
                }

                return null;
            },
            THREAD_COUNT
        );
    }

    private List<TriWrapper<String, String, Map<String, Object>>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_ID, COLUMN_LAST_UPDATE, COLUMN_STAR_SYSTEM_ID, COLUMN_CARRIER_ID, COLUMN_DOCKING_ACCESS, COLUMN_MARKET_ID)
            .from(new QualifiedTable(SCHEMA, TABLE_FLEET_CARRIER))
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
                        new BiWrapper<>(COLUMN_CARRIER_ID, rs.getString(COLUMN_CARRIER_ID)),
                        new BiWrapper<>(COLUMN_DOCKING_ACCESS, rs.getString(COLUMN_DOCKING_ACCESS)),
                        new BiWrapper<>(COLUMN_MARKET_ID, rs.getLong(COLUMN_MARKET_ID))
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
