package com.github.saphyra.apphub.service.feature.elite_base.migration;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
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

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_LAST_UPDATE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_OBJECT_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.MAX_BATCH_SIZE;

@RequiredArgsConstructor
@Component
@Slf4j
@Profile("!test")
class LastUpdateMigrator {
    //Ensure migration runs after partitions are created
    @SuppressWarnings("unused")
    private final LastUpdatePartitionCreator lastUpdatePartitionCreator;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating LastUpdates to V2...");

        int totalCount = 0;
        List<Map<String, String>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to LastUpdateV2", totalCount);
    }

    private void delete(List<Map<String, String>> batch) {
        String sql = "DELETE FROM %s.%s WHERE %s = :%s AND %s = :%s".formatted(
            SCHEMA,
            TABLE_LAST_UPDATE,
            COLUMN_EXTERNAL_REFERENCE,
            COLUMN_EXTERNAL_REFERENCE,
            COLUMN_TYPE,
            COLUMN_OBJECT_TYPE
        );

        try {
            jdbcTemplate.batchUpdate(
                sql,
                batch.toArray(Map[]::new)
            );
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private void save(List<Map<String, String>> batch) {
        if (batch.isEmpty()) {
            return;
        }

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE_V2), batch.get(0).keySet()).build();

        try {
            jdbcTemplate.batchUpdate(
                sql,
                batch.toArray(Map[]::new)
            );
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private List<Map<String, String>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_EXTERNAL_REFERENCE, COLUMN_TYPE, COLUMN_LAST_UPDATE)
            .from(new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<Map<String, String>> result = new ArrayList<>();
                while (rs.next()) {
                    Map<String, String> map = Map.of(
                        COLUMN_EXTERNAL_REFERENCE, rs.getString(COLUMN_EXTERNAL_REFERENCE),
                        COLUMN_OBJECT_TYPE, rs.getString(COLUMN_TYPE),
                        COLUMN_LAST_UPDATE, rs.getString(COLUMN_LAST_UPDATE)
                    );
                    result.add(map);
                }
                return result;
            }
        );
    }
}
