package com.github.saphyra.apphub.service.feature.elite_base.processor.migration.migrator;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_BODY_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_MATERIAL;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.COLUMN_PERCENT;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_BODY_MATERIAL;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_BODY_MATERIAL_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.migration.MigratorConstants.MAX_BATCH_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
class BodyMaterialMigrator {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating BodyMaterial to V2...");

        int totalCount = 0;
        List<BiWrapper<String, Map<String, Object>>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to BodyMaterialV2", totalCount);
    }

    private void save(List<BiWrapper<String, Map<String, Object>>> batch) {
        if (batch.isEmpty()) {
            return;
        }

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_BODY_MATERIAL_V2), batch.getFirst().getEntity2().keySet()).build();
        Map<String, Object>[] params = batch.stream()
            .map(BiWrapper::getEntity2)
            .toArray(Map[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private void delete(List<BiWrapper<String, Map<String, Object>>> batch) {
        if (batch.isEmpty()) {
            return;
        }

        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_BODY_MATERIAL))
            .condition(new Equation(new DefaultColumn(COLUMN_ID), () -> ":id"))
            .build();

        Map<String, Object>[] params = batch.stream()
            .map(row -> Map.of("id", row.getEntity1()))
            .toArray(Map[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private List<BiWrapper<String, Map<String, Object>>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_ID, COLUMN_BODY_ID, COLUMN_MATERIAL, COLUMN_PERCENT)
            .from(new QualifiedTable(SCHEMA, TABLE_BODY_MATERIAL))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<BiWrapper<String, Map<String, Object>>> result = new ArrayList<>();
                while (rs.next()) {
                    String id = rs.getString(COLUMN_ID);
                    Map<String, Object> row = Map.of(
                        COLUMN_BODY_ID, rs.getString(COLUMN_BODY_ID),
                        COLUMN_MATERIAL, rs.getString(COLUMN_MATERIAL),
                        COLUMN_PERCENT, rs.getBigDecimal(COLUMN_PERCENT).doubleValue()
                    );
                    result.add(new BiWrapper<>(id, row));
                }
                return result;
            }
        );
    }
}
