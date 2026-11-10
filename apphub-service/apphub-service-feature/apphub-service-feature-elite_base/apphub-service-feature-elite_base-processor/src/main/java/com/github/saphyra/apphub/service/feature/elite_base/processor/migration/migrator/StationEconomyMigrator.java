package com.github.saphyra.apphub.service.feature.elite_base.processor.migration.migrator;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_ECONOMY;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_PROPORTION;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_STATION_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_STATION_ECONOMY;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_STATION_ECONOMY_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.migration.MigratorConstants.MAX_BATCH_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class StationEconomyMigrator {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating StationEconomy to V2...");

        int totalCount = 0;
        List<Map<String, Object>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to StationEconomy V2", totalCount);
    }

    private void delete(List<Map<String, Object>> batch) {
        String sql = "DELETE FROM %s.%s WHERE %s = :%s AND %s = :%s".formatted(
            SCHEMA,
            TABLE_STATION_ECONOMY,
            COLUMN_STATION_ID,
            COLUMN_STATION_ID,
            COLUMN_ECONOMY,
            COLUMN_ECONOMY
        );

        try {
            jdbcTemplate.batchUpdate(sql, batch.toArray(Map[]::new));
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private void save(List<Map<String, Object>> entities) {
        if (entities.isEmpty()) {
            return;
        }

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_STATION_ECONOMY_V2), entities.getFirst().keySet()).build();
        try {
            jdbcTemplate.batchUpdate(sql, entities.toArray(Map[]::new));
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private List<Map<String, Object>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_STATION_ID, COLUMN_ECONOMY, COLUMN_PROPORTION)
            .from(new QualifiedTable(SCHEMA, TABLE_STATION_ECONOMY))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<Map<String, Object>> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(
                        CollectionUtils.toMap(
                            new BiWrapper<>(COLUMN_STATION_ID, rs.getString(COLUMN_STATION_ID)),
                            new BiWrapper<>(COLUMN_ECONOMY, rs.getString(COLUMN_ECONOMY)),
                            new BiWrapper<>(COLUMN_PROPORTION, rs.getBigDecimal(COLUMN_PROPORTION))
                        )
                    );
                }
                return result;
            }
        );
    }
}

