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

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_BODY_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_INNER_RADIUS;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_MASS;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_NAME;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_OUTER_RADIUS;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_BODY_RING;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_BODY_RING_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.migration.MigratorConstants.MAX_BATCH_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class BodyRingMigrator {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating BodyRing to V2...");

        int totalCount = 0;
        List<Map<String, Object>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to BodyRingV2", totalCount);
    }

    private void save(List<Map<String, Object>> batch) {
        if (batch.isEmpty()) {
            return;
        }

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_BODY_RING_V2), batch.getFirst().keySet()).build();
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
            .from(new QualifiedTable(SCHEMA, TABLE_BODY_RING))
            .condition(new Equation(new DefaultColumn(COLUMN_ID), () -> ":id"))
            .build();

        Map<String, Object>[] params = batch.stream()
            .map(row -> Map.of("id", row.get(COLUMN_ID)))
            .toArray(Map[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private List<Map<String, Object>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_ID, COLUMN_BODY_ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_INNER_RADIUS, COLUMN_OUTER_RADIUS, COLUMN_MASS)
            .from(new QualifiedTable(SCHEMA, TABLE_BODY_RING))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<Map<String, Object>> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(CollectionUtils.toMap(
                        new BiWrapper<>(COLUMN_ID, rs.getString(COLUMN_ID)),
                        new BiWrapper<>(COLUMN_BODY_ID, rs.getString(COLUMN_BODY_ID)),
                        new BiWrapper<>(COLUMN_NAME, rs.getString(COLUMN_NAME)),
                        new BiWrapper<>(COLUMN_TYPE, rs.getString(COLUMN_TYPE)),
                        new BiWrapper<>(COLUMN_INNER_RADIUS, Optional.ofNullable(rs.getBigDecimal(COLUMN_INNER_RADIUS)).map(Number::doubleValue).orElse(null)),
                        new BiWrapper<>(COLUMN_OUTER_RADIUS, Optional.ofNullable(rs.getBigDecimal(COLUMN_OUTER_RADIUS)).map(Number::doubleValue).orElse(null)),
                        new BiWrapper<>(COLUMN_MASS, Optional.ofNullable(rs.getBigDecimal(COLUMN_MASS)).map(Number::doubleValue).orElse(null))
                    ));
                }
                return result;
            }
        );
    }
}
