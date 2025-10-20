package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.dao.BatchOrphanedRecordCleaner;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Orphanage;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.DefaultColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.DistinctColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.InCondition;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.ListValue;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedTable;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_POWER_MAPPING;

@Component
@Slf4j
class StarSystemPowerMappingOrphanedRecordCleaner extends BatchOrphanedRecordCleaner {
    private final JdbcTemplate jdbcTemplate;
    private final EliteBaseProperties eliteBaseProperties;

    StarSystemPowerMappingOrphanedRecordCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate, EliteBaseProperties eliteBaseProperties) {
        super(errorReporterService, eliteBaseProperties);
        this.jdbcTemplate = jdbcTemplate;
        this.eliteBaseProperties = eliteBaseProperties;
    }

    @Override
    public Orphanage getOrphanage() {
        return Orphanage.STAR_SYSTEM_POWER_MAPPING;
    }

    @Override
    public List<Orphanage> getPreconditions() {
        return List.of();
    }

    @Override
    protected List<String> fetchIds() {
        String sql = SqlBuilder.select()
            .column(new DistinctColumn(new DefaultColumn(COLUMN_STAR_SYSTEM_ID)))
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_POWER_MAPPING))
            .except(SqlBuilder.select().column(new DefaultColumn(COLUMN_ID)).from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM)))
            .limit(eliteBaseProperties.getOrphanedRecordCleaner().getBatchSize())
            .build();

        return jdbcTemplate.query(sql, rs -> {
            List<String> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rs.getString(COLUMN_STAR_SYSTEM_ID));
            }
            return result;
        });
    }

    @Override
    protected void delete(List<String> idsToDelete) {
        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_POWER_MAPPING))
            .condition(new InCondition(new DefaultColumn(COLUMN_STAR_SYSTEM_ID), new ListValue(idsToDelete)))
            .build();

        jdbcTemplate.update(sql);
    }
}
