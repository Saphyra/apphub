package com.github.saphyra.apphub.service.custom.elite_base.dao.station;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.OrphanedRecordCleaner;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.Equation;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.NotExistsCondition;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedTable;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STATION;

@Component
@Slf4j
class StationOrphanedRecordCleaner extends OrphanedRecordCleaner {
    private final JdbcTemplate jdbcTemplate;

    StationOrphanedRecordCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate) {
        super(errorReporterService);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected int doCleanup() {
        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_STATION))
            .condition(new NotExistsCondition(SqlBuilder.select()
                .column("1")
                .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM))
                .condition(new Equation(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_ID), new QualifiedColumn(TABLE_STATION, COLUMN_STAR_SYSTEM_ID)))
            ))
            .build();

        return jdbcTemplate.update(sql);
    }
}
