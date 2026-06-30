package com.github.saphyra.apphub.service.feature.elite_base.migration;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.value.WrappedValue;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdatePartitionCreator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_POWER;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_POWER_MAPPING;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_POWER_MAPPING_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.MAX_BATCH_SIZE;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.THREAD_COUNT;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class StarSystemPowerMappingMigrator {
    //Ensure migration runs after partitions are created
    @SuppressWarnings("unused")
    private final LastUpdatePartitionCreator lastUpdatePartitionCreator;
    private final JdbcTemplate jdbcTemplate;
    private final ExecutorServiceBean executorServiceBean;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating StarSystemPowerMappings to V2...");

        int totalCount = 0;
        List<Map<String, String>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to StarSystemPowerMappingsV2", totalCount);
    }

    private void delete(List<Map<String, String>> batch) {
        List<String> sqls = batch.stream()
            .map(entry -> SqlBuilder.delete()
                .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_POWER_MAPPING))
                .condition(new Equation(new DefaultColumn(COLUMN_STAR_SYSTEM_ID), new WrappedValue(entry.get(COLUMN_STAR_SYSTEM_ID))))
                .and()
                .condition(new Equation(new DefaultColumn(COLUMN_POWER), new WrappedValue(entry.get(COLUMN_POWER))))
                .build()
            )
            .toList();

        executorServiceBean.processCollectionWithWait(sqls, jdbcTemplate::update, THREAD_COUNT);
    }

    private void save(List<Map<String, String>> batch) {
        List<String> sqls = batch.stream()
            .map(entry -> SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_POWER_MAPPING_V2), entry).build())
            .toList();

        executorServiceBean.processCollectionWithWait(sqls, this::save, THREAD_COUNT);
    }

    private Void save(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }

        return null;
    }

    private List<Map<String, String>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_STAR_SYSTEM_ID, COLUMN_POWER)
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_POWER_MAPPING))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<Map<String, String>> result = new ArrayList<>();
                while (rs.next()) {
                    Map<String, String> map = Map.of(
                        COLUMN_STAR_SYSTEM_ID, rs.getString(COLUMN_STAR_SYSTEM_ID),
                        COLUMN_POWER, rs.getString(COLUMN_POWER)
                    );
                    result.add(map);
                }
                return result;
            }
        );
    }
}
