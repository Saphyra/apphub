package com.github.saphyra.apphub.service.feature.elite_base.migration;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.condition.NotNullCondition;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.value.NullValue;
import com.github.saphyra.apphub.lib.sql_builder.value.WrappedValue;
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
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.MAX_BATCH_SIZE;
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.THREAD_COUNT;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class StarSystemLastUpdateMigrator {
    //Ensure migration runs after partitions are created
    @SuppressWarnings("unused")
    private final LastUpdatePartitionCreator lastUpdatePartitionCreator;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ExecutorServiceBean executorServiceBean;

    @PostConstruct
    void migrate() {
        log.info("Migrating StarSystem lastUpdates");

        int totalCount = 0;
        List<BiWrapper<String, String>> batch;
        do {
            batch = fetchBatch();
            totalCount += batch.size();

            saveLastUpdate(batch);
            clearLastUpdate(batch);
        } while (!batch.isEmpty());

        log.info("{} StarSystems migrated", totalCount);
    }

    private void clearLastUpdate(List<BiWrapper<String, String>> batch) {
        List<String> sqls = batch.stream()
            .map(entry -> SqlBuilder.update(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM))
                .set(new DefaultColumn(COLUMN_LAST_UPDATE), new NullValue())
                .condition(new Equation(new DefaultColumn(COLUMN_ID), new WrappedValue(entry.getEntity1())))
                .build())
            .toList();

        executorServiceBean.processCollectionWithWait(sqls, sql -> jdbcTemplate.update(sql, Map.of()), THREAD_COUNT);
    }

    private void saveLastUpdate(List<BiWrapper<String, String>> batch) {
        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE_V2), List.of(COLUMN_EXTERNAL_REFERENCE, COLUMN_OBJECT_TYPE, COLUMN_LAST_UPDATE)).build();

        executorServiceBean.processCollectionWithWait(
            batch,
            record -> {
                Map<String, String> parameters = Map.of(
                    COLUMN_EXTERNAL_REFERENCE, record.getEntity1(),
                    COLUMN_OBJECT_TYPE, ObjectType.STAR_SYSTEM.name(),
                    COLUMN_LAST_UPDATE, record.getEntity2()
                );
                jdbcTemplate.update(sql, parameters);

                return null;
            },
            THREAD_COUNT
        );
    }

    private List<BiWrapper<String, String>> fetchBatch() {
        String sql = SqlBuilder.select()
            .columns(COLUMN_ID, COLUMN_LAST_UPDATE)
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM))
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
