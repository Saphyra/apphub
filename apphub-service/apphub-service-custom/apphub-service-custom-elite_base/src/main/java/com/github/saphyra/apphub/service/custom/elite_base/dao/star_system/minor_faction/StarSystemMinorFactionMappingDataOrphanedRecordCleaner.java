package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction;

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
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_MINOR_FACTION_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_MINOR_FACTION;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_DATA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_MINOR_FACTION_MAPPING;

@Component
@Slf4j
class StarSystemMinorFactionMappingDataOrphanedRecordCleaner extends OrphanedRecordCleaner {
    private final JdbcTemplate jdbcTemplate;

    StarSystemMinorFactionMappingDataOrphanedRecordCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate) {
        super(errorReporterService);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected int doCleanup() {
        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_MINOR_FACTION_MAPPING))
            .condition(new NotExistsCondition(SqlBuilder.select()
                .column("1")
                .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_DATA))
                .condition(new Equation(new QualifiedColumn(TABLE_STAR_SYSTEM_DATA, COLUMN_STAR_SYSTEM_ID), new QualifiedColumn(TABLE_STAR_SYSTEM_MINOR_FACTION_MAPPING, COLUMN_STAR_SYSTEM_ID)))
            ))
            .or()
            .condition(new NotExistsCondition(SqlBuilder.select()
                .column("1")
                .from(new QualifiedTable(SCHEMA, TABLE_MINOR_FACTION))
                .condition(new Equation(new QualifiedColumn(TABLE_MINOR_FACTION, COLUMN_ID), new QualifiedColumn(TABLE_STAR_SYSTEM_MINOR_FACTION_MAPPING, COLUMN_MINOR_FACTION_ID)))
            ))
            .build();

        return jdbcTemplate.update(sql);
    }
}
