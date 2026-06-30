package com.github.saphyra.apphub.service.feature.elite_base.migration;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
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
import org.springframework.jdbc.core.JdbcTemplate;
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
import static com.github.saphyra.apphub.service.feature.elite_base.migration.MigratorConstants.THREAD_COUNT;

@RequiredArgsConstructor
@Component
@Slf4j
@Profile("!test")
class LastUpdateToV2Migrator {
    //Ensure migration runs after partitions are created
    @SuppressWarnings("unused")
    private final LastUpdatePartitionCreator lastUpdatePartitionCreator;
    private final JdbcTemplate jdbcTemplate;
    private final ExecutorServiceBean executorServiceBean;

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
        } while (!batch.isEmpty());

        log.info("{} records are migrated to LastUpdateV2", totalCount);
    }

    private void delete(List<Map<String, String>> batch) {
        List<String> sqls = batch.stream()
            .map(entry -> SqlBuilder.delete()
                .from(new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE))
                .condition(new Equation(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE), new WrappedValue(entry.get(COLUMN_EXTERNAL_REFERENCE))))
                .and()
                .condition(new Equation(new DefaultColumn(COLUMN_TYPE), new WrappedValue(entry.get(COLUMN_OBJECT_TYPE))))
                .build()
            )
            .toList();

        executorServiceBean.processCollectionWithWait(sqls, jdbcTemplate::update, THREAD_COUNT);
    }

    private void save(List<Map<String, String>> batch) {
        List<String> sqls = batch.stream()
            .map(entry -> SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE_V2), entry).build())
            .toList();

        executorServiceBean.processCollectionWithWait(sqls, jdbcTemplate::update, THREAD_COUNT);
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
