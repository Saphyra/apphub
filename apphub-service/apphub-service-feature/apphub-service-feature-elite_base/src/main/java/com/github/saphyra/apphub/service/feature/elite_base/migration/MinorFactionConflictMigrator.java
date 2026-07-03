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

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STATUS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_WAR_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_MINOR_FACTION_CONFLICT;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_MINOR_FACTION_CONFLICT_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.MAX_BATCH_SIZE;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.THREAD_COUNT;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class MinorFactionConflictMigrator {
    //Ensure migration runs after partitions are created
    @SuppressWarnings("unused")
    private final LastUpdatePartitionCreator lastUpdatePartitionCreator;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ExecutorServiceBean executorServiceBean;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating MinorFactionConflicts to V2...");

        int totalCount = 0;
        List<Map<String, String>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to MinorFactionConflictV2", totalCount);
    }

    private void delete(List<Map<String, String>> batch) {
        List<String> sqls = batch.stream()
            .map(entry -> SqlBuilder.delete()
                .from(new QualifiedTable(SCHEMA, TABLE_MINOR_FACTION_CONFLICT))
                .condition(new Equation(new DefaultColumn(COLUMN_ID), new WrappedValue(entry.get(COLUMN_ID))))
                .build()
            )
            .toList();

        executorServiceBean.processCollectionWithWait(sqls, sql -> jdbcTemplate.update(sql, Map.of()), THREAD_COUNT);
    }

    private void save(List<Map<String, String>> batch) {
        executorServiceBean.processCollectionWithWait(
            batch,
            record -> {
                String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_MINOR_FACTION_CONFLICT_V2), record.keySet()).build();

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

    private List<Map<String, String>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_ID, COLUMN_STAR_SYSTEM_ID, COLUMN_STATUS, COLUMN_WAR_TYPE)
            .from(new QualifiedTable(SCHEMA, TABLE_MINOR_FACTION_CONFLICT))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<Map<String, String>> result = new ArrayList<>();
                while (rs.next()) {
                    Map<String, String> map = CollectionUtils.toMap(
                        new BiWrapper<>(COLUMN_ID, rs.getString(COLUMN_ID)),
                        new BiWrapper<>(COLUMN_STAR_SYSTEM_ID, rs.getString(COLUMN_STAR_SYSTEM_ID)),
                        new BiWrapper<>(COLUMN_STATUS, rs.getString(COLUMN_STATUS)),
                        new BiWrapper<>(COLUMN_WAR_TYPE, rs.getString(COLUMN_WAR_TYPE))
                    );
                    result.add(map);
                }
                return result;
            }
        );
    }
}
