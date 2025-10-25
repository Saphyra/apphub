package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict;

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
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_POWERPLAY_CONFLICT;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;

@Component
@Slf4j
class PowerplayConflictOrphanedRecordCleaner extends BatchOrphanedRecordCleaner {
    private final JdbcTemplate jdbcTemplate;
    private final EliteBaseProperties eliteBaseProperties;

    PowerplayConflictOrphanedRecordCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate, EliteBaseProperties eliteBaseProperties) {
        super(errorReporterService, eliteBaseProperties);
        this.jdbcTemplate = jdbcTemplate;
        this.eliteBaseProperties = eliteBaseProperties;
    }

    @Override
    public Orphanage getOrphanage() {
        return Orphanage.POWERPLAY_CONFLICT;
    }

    @Override
    public List<Orphanage> getPreconditions() {
        return List.of();
    }

    @Override
    protected List<String> fetchIds() {
        String sql = SqlBuilder.select()
            .column(new DistinctColumn(new DefaultColumn(COLUMN_STAR_SYSTEM_ID)))
            .from(new QualifiedTable(SCHEMA, TABLE_POWERPLAY_CONFLICT))
            .except(SqlBuilder.select().column(COLUMN_ID).from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM)))
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
            .from(new QualifiedTable(SCHEMA, TABLE_POWERPLAY_CONFLICT))
            .condition(new InCondition(new DefaultColumn(COLUMN_STAR_SYSTEM_ID), new ListValue(idsToDelete)))
            .build();

        jdbcTemplate.update(sql);
    }
}
