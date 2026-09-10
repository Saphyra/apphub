package com.github.saphyra.apphub.service.feature.elite_base.migration;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_CONFLICT_PROGRESS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_POWER;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_POWERPLAY_CONFLICT;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_POWERPLAY_CONFLICT_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.MAX_BATCH_SIZE;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.THREAD_COUNT;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class PowerplayConflictMigrator {
    //Ensure migration runs after partitions are created
    @SuppressWarnings("unused")
    private final LastUpdatePartitionCreator lastUpdatePartitionCreator;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ExecutorServiceBean executorServiceBean;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating PowerplayConflicts to V2...");

        int totalCount = 0;
        List<Map<String, Object>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to PowerplayConflictV2", totalCount);
    }

    private void delete(List<Map<String, Object>> batch) {
        List<String> sqls = batch.stream()
            .map(entry -> SqlBuilder.delete()
                .from(new QualifiedTable(SCHEMA, TABLE_POWERPLAY_CONFLICT))
                .condition(new Equation(new DefaultColumn(COLUMN_STAR_SYSTEM_ID), new WrappedValue(entry.get(COLUMN_STAR_SYSTEM_ID))))
                .and()
                .condition(new Equation(new DefaultColumn(COLUMN_POWER), new WrappedValue(entry.get(COLUMN_POWER))))
                .build()
            )
            .toList();

        executorServiceBean.processCollectionWithWait(sqls, sql -> jdbcTemplate.update(sql, Map.of()), THREAD_COUNT);
    }

    private void save(List<Map<String, Object>> batch) {
        executorServiceBean.processCollectionWithWait(
            batch,
            record -> {
                String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_POWERPLAY_CONFLICT_V2), record.keySet()).build();

                try {
                    jdbcTemplate.update(sql, record);
                } catch (Exception e) {
                    errorReporterService.report("Failed to execute SQL: " + sql, e);
                }

                return null;
            },
            THREAD_COUNT
        );
    }

    private List<Map<String, Object>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_STAR_SYSTEM_ID, COLUMN_POWER, COLUMN_CONFLICT_PROGRESS)
            .from(new QualifiedTable(SCHEMA, TABLE_POWERPLAY_CONFLICT))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<Map<String, Object>> result = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> map = CollectionUtils.toMap(
                        new BiWrapper<>(COLUMN_STAR_SYSTEM_ID, rs.getString(COLUMN_STAR_SYSTEM_ID)),
                        new BiWrapper<>(COLUMN_POWER, rs.getString(COLUMN_POWER)),
                        new BiWrapper<>(COLUMN_CONFLICT_PROGRESS, rs.getDouble(COLUMN_CONFLICT_PROGRESS))
                    );
                    result.add(map);
                }
                return result;
            }
        );
    }
}
