package com.github.saphyra.apphub.service.feature.elite_base.migration;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
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

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_POWER;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_MINOR_FACTION_MAPPING_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_POWER_MAPPING;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.MAX_BATCH_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class StarSystemPowerMappingMigrator {
    //Ensure migration runs after partitions are created
    @SuppressWarnings("unused")
    private final LastUpdatePartitionCreator lastUpdatePartitionCreator;
    private final NamedParameterJdbcTemplate jdbcTemplate;
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
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to StarSystemPowerMappingV2", totalCount);
    }

    private void delete(List<Map<String, String>> batch) {
        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_POWER_MAPPING))
            .condition(new Equation(new DefaultColumn(COLUMN_STAR_SYSTEM_ID), () -> ":starSystemId"))
            .and()
            .condition(new Equation(new DefaultColumn(COLUMN_POWER), () -> ":power"))
            .build();

        Map<String, Object>[] params = batch.stream()
            .map(entry -> Map.<String, Object>of(
                "starSystemId", entry.get(COLUMN_STAR_SYSTEM_ID),
                "power", entry.get(COLUMN_POWER)
            ))
            .toArray(Map[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private void save(List<Map<String, String>> batch) {
        if (batch.isEmpty()) {
            return;
        }

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_MINOR_FACTION_MAPPING_V2), batch.get(0).keySet()).build();
        Map<String, Object>[] params = batch.stream()
            .map(record -> Map.<String, Object>of(
                COLUMN_STAR_SYSTEM_ID, record.get(COLUMN_STAR_SYSTEM_ID),
                COLUMN_POWER, record.get(COLUMN_POWER)
            ))
            .toArray(Map[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
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
