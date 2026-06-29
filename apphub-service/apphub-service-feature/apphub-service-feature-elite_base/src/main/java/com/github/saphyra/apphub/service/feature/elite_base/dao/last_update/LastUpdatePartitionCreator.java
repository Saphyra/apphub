package com.github.saphyra.apphub.service.feature.elite_base.dao.last_update;

import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import jakarta.annotation.PostConstruct;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE_V2;

@Component
@RequiredArgsConstructor
@Profile("!test")
@Slf4j
public class LastUpdatePartitionCreator {
    //Ensure migration runs after liquibase is finished
    @SuppressWarnings("unused")
    private final SpringLiquibase liquibase;
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    void createTables() {
        List<ObjectType> objectTypes = Arrays.asList(ObjectType.values());
        log.info("Creating partition tables for ObjectTypes {} for table {}", objectTypes, TABLE_LAST_UPDATE_V2);

        objectTypes.forEach(this::createTable);

        log.info("Partition creation finished for table {}", TABLE_LAST_UPDATE_V2);
    }

    private void createTable(ObjectType objectType) {
        log.info("Creating partition table for ObjectType {}", objectType);

        String tableName = TABLE_LAST_UPDATE_V2 + "_" + objectType.name().toLowerCase();

        String sql = """
            DO $$
            BEGIN
                IF to_regclass('${schema}.${partitionName}') IS NULL THEN
                    CREATE TABLE ${schema}.${partitionName}
                    PARTITION OF ${schema}.${parentName}
                    FOR VALUES IN ('${value}');
                END IF;
            END
            $$;
            """
            .replace("${schema}", SCHEMA)
            .replace("${partitionName}", tableName)
            .replace("${parentName}", TABLE_LAST_UPDATE_V2)
            .replace("${value}", objectType.name());

        log.debug("Executing SQL {}", sql);
        jdbcTemplate.update(sql);

        log.info("Partition created for ObjectType {}", objectType);
    }
}
