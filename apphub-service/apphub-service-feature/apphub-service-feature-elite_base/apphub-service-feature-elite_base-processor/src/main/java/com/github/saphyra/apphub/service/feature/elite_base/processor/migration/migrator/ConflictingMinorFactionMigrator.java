package com.github.saphyra.apphub.service.feature.elite_base.processor.migration.migrator;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
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
import java.util.Optional;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_CONFLICT_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_MINOR_FACTION_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_STAKE;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_WON_DAYS;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_CONFLICTING_MINOR_FACTION;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_CONFLICTING_MINOR_FACTION_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.migration.MigratorConstants.MAX_BATCH_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class ConflictingMinorFactionMigrator {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating ConflictingMinorFaction to V2...");

        int totalCount = 0;
        List<Map<String, Object>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to ConflictingMinorFactionV2", totalCount);
    }

    private void save(List<Map<String, Object>> batch) {
        if (batch.isEmpty()) {
            return;
        }

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_CONFLICTING_MINOR_FACTION_V2), batch.getFirst().keySet()).build();
        Map<String, Object>[] params = batch.toArray(Map[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private void delete(List<Map<String, Object>> batch) {
        if (batch.isEmpty()) {
            return;
        }

        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_CONFLICTING_MINOR_FACTION))
            .condition(new Equation(new DefaultColumn(COLUMN_CONFLICT_ID), () -> ":conflictId"))
            .and()
            .condition(new Equation(new DefaultColumn(COLUMN_MINOR_FACTION_ID), () -> ":minorFactionId"))
            .build();

        Map<String, Object>[] params = batch.stream()
            .map(row -> Map.of("conflictId", row.get(COLUMN_CONFLICT_ID), "minorFactionId", row.get(COLUMN_MINOR_FACTION_ID)))
            .toArray(Map[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private List<Map<String, Object>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_CONFLICT_ID, COLUMN_MINOR_FACTION_ID, COLUMN_WON_DAYS, COLUMN_STAKE)
            .from(new QualifiedTable(SCHEMA, TABLE_CONFLICTING_MINOR_FACTION))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<Map<String, Object>> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(CollectionUtils.toMap(
                        new BiWrapper<>(COLUMN_CONFLICT_ID, rs.getString(COLUMN_CONFLICT_ID)),
                        new BiWrapper<>(COLUMN_MINOR_FACTION_ID, rs.getString(COLUMN_MINOR_FACTION_ID)),
                        new BiWrapper<>(COLUMN_WON_DAYS, Optional.ofNullable(rs.getBigDecimal(COLUMN_WON_DAYS)).map(Number::doubleValue).orElse(null)),
                        new BiWrapper<>(COLUMN_STAKE, rs.getString(COLUMN_STAKE))
                    ));
                }
                return result;
            }
        );
    }
}
