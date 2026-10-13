package com.github.saphyra.apphub.service.feature.elite_base.migration.migrator;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_BODY_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_LAST_UPDATE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_OBJECT_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_BODY_DATA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_BODY_DATA_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.MAX_BATCH_SIZE;

@RequiredArgsConstructor
@Component
@Slf4j
@Profile("!test")
class BodyDataMigrator {
    private static final String COLUMN_LANDABLE = "landable";
    private static final String COLUMN_SURFACE_GRAVITY = "surface_gravity";
    private static final String COLUMN_RESERVE_LEVEL = "reserve_level";
    private static final String COLUMN_HAS_RING = "has_ring";

    //Ensure migration runs after bodies are migrated to V3
    @SuppressWarnings("unused")
    private final BodyToV3Migrator bodyToV3Migrator;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Migrating BodyData to V2...");

        int totalCount = 0;
        List<TriWrapper<String, String, Map<String, Object>>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            save(batch);
            delete(batch);
            log.info("Processed batch of size {}. Total: {}", batch.size(), totalCount);
        } while (!batch.isEmpty());

        log.info("{} records are migrated to BodyDataV2", totalCount);
    }

    private void save(List<TriWrapper<String, String, Map<String, Object>>> batch) {
        List<BiWrapper<String, String>> lastUpdates = batch.stream()
            .map(entry -> new BiWrapper<>(entry.getEntity1(), entry.getEntity2()))
            .toList();
        saveLastUpdates(lastUpdates);

        List<Map<String, Object>> entities = batch.stream()
            .map(TriWrapper::getEntity3)
            .toList();
        saveEntities(entities);
    }

    private void saveLastUpdates(List<BiWrapper<String, String>> lastUpdates) {
        String sql = SqlBuilder.insert(
                new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE_V2),
                List.of(COLUMN_EXTERNAL_REFERENCE, COLUMN_LAST_UPDATE, COLUMN_OBJECT_TYPE)
            )
            .build();

        Map<String, Object>[] params = lastUpdates.stream()
            .map(record -> Map.<String, Object>of(
                COLUMN_EXTERNAL_REFERENCE, record.getEntity1(),
                COLUMN_LAST_UPDATE, record.getEntity2(),
                COLUMN_OBJECT_TYPE, ObjectType.BODY_DATA.name()
            ))
            .toArray(Map[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private void saveEntities(List<Map<String, Object>> entities) {
        if (entities.isEmpty()) {
            return;
        }

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_BODY_DATA_V2), entities.getFirst().keySet()).build();
        Map<String, Object>[] params = entities.toArray(Map[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private void delete(List<TriWrapper<String, String, Map<String, Object>>> batch) {
        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_BODY_DATA))
            .condition(new Equation(new DefaultColumn(COLUMN_BODY_ID), () -> ":bodyId"))
            .build();

        Map<String, Object>[] params = batch.stream()
            .map(TriWrapper::getEntity1)
            .map(bodyId -> Map.<String, Object>of("bodyId", bodyId))
            .toArray(Map[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception e) {
            errorReporterService.report("Failed to execute SQL: " + sql, e);
        }
    }

    private List<TriWrapper<String, String, Map<String, Object>>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_BODY_ID, COLUMN_LAST_UPDATE, COLUMN_LANDABLE, COLUMN_SURFACE_GRAVITY, COLUMN_RESERVE_LEVEL, COLUMN_HAS_RING)
            .from(new QualifiedTable(SCHEMA, TABLE_BODY_DATA))
            .limit(MAX_BATCH_SIZE)
            .build();

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<TriWrapper<String, String, Map<String, Object>>> result = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> entity = CollectionUtils.toMap(
                        new BiWrapper<>(COLUMN_BODY_ID, rs.getString(COLUMN_BODY_ID)),
                        new BiWrapper<>(COLUMN_LANDABLE, rs.getObject(COLUMN_LANDABLE, Boolean.class)),
                        new BiWrapper<>(COLUMN_SURFACE_GRAVITY, rs.getObject(COLUMN_SURFACE_GRAVITY, Double.class)),
                        new BiWrapper<>(COLUMN_RESERVE_LEVEL, rs.getString(COLUMN_RESERVE_LEVEL)),
                        new BiWrapper<>(COLUMN_HAS_RING, rs.getObject(COLUMN_HAS_RING, Boolean.class))
                    );

                    result.add(new TriWrapper<>(
                        rs.getString(COLUMN_BODY_ID),
                        rs.getString(COLUMN_LAST_UPDATE),
                        entity
                    ));
                }
                return result;
            }
        );
    }
}

