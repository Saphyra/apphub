package com.github.saphyra.apphub.service.feature.elite_base.migration;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.condition.NotNullCondition;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
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

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_LAST_UPDATE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_OBJECT_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_MINOR_FACTION;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.MAX_BATCH_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class MinorFactionLastUpdateMigrator {
    //Ensure migration runs after partitions are created
    @SuppressWarnings("unused")
    private final LastUpdatePartitionCreator lastUpdatePartitionCreator;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating MinorFaction lastUpdates");

        int totalCount = 0;
        List<BiWrapper<String, String>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            saveLastUpdate(batch);
            clearLastUpdate(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} MinorFactions migrated", totalCount);
    }

    private void clearLastUpdate(List<BiWrapper<String, String>> batch) {
        String sql = "UPDATE %s.%s SET %s = NULL WHERE %s = :%s".formatted(
            SCHEMA,
            TABLE_MINOR_FACTION,
            COLUMN_LAST_UPDATE,
            COLUMN_ID,
            COLUMN_ID
        );

        try {
            jdbcTemplate.batchUpdate(
                sql,
                batch.stream()
                    .map(entry -> Map.<String, Object>of(COLUMN_ID, entry.getEntity1()))
                    .toArray(Map[]::new)
            );
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private void saveLastUpdate(List<BiWrapper<String, String>> batch) {
        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE_V2), List.of(COLUMN_EXTERNAL_REFERENCE, COLUMN_OBJECT_TYPE, COLUMN_LAST_UPDATE)).build();

        try {
            jdbcTemplate.batchUpdate(
                sql,
                batch.stream()
                    .map(record -> Map.<String, Object>of(
                        COLUMN_EXTERNAL_REFERENCE, record.getEntity1(),
                        COLUMN_OBJECT_TYPE, ObjectType.MINOR_FACTION.name(),
                        COLUMN_LAST_UPDATE, record.getEntity2()
                    ))
                    .toArray(Map[]::new)
            );
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private List<BiWrapper<String, String>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_ID, COLUMN_LAST_UPDATE)
            .from(new QualifiedTable(SCHEMA, TABLE_MINOR_FACTION))
            .condition(new NotNullCondition(new DefaultColumn(COLUMN_LAST_UPDATE)))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<BiWrapper<String, String>> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(new BiWrapper<>(
                        rs.getString(COLUMN_ID),
                        rs.getString(COLUMN_LAST_UPDATE)
                    ));
                }

                return result;
            }
        );
    }
}
