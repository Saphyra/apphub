package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.minor_faction;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Orphanage;
import com.github.saphyra.apphub.service.custom.elite_base.dao.OrphanedRecordCleaner;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.Equation;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.NotExistsCondition;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedTable;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_CONFLICT_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_MINOR_FACTION_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_CONFLICTING_MINOR_FACTION;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_MINOR_FACTION;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_MINOR_FACTION_CONFLICT;

@Component
@Slf4j
class ConflictingMinorFactionOrphanedRecordCleaner extends OrphanedRecordCleaner {
    private final JdbcTemplate jdbcTemplate;

    ConflictingMinorFactionOrphanedRecordCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate) {
        super(errorReporterService);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Orphanage getOrphanage() {
        return Orphanage.CONFLICTING_MINOR_FACTION;
    }

    @Override
    public List<Orphanage> getPreconditions() {
        return List.of(Orphanage.MINOR_FACTION, Orphanage.MINOR_FACTION_CONFLICT);
    }

    @Override
    protected int doCleanup() {
        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_CONFLICTING_MINOR_FACTION))
            .condition(new NotExistsCondition(SqlBuilder.select()
                .column("1")
                .from(new QualifiedTable(SCHEMA, TABLE_MINOR_FACTION))
                .condition(new Equation(new QualifiedColumn(TABLE_MINOR_FACTION, COLUMN_ID), new QualifiedColumn(TABLE_CONFLICTING_MINOR_FACTION, COLUMN_MINOR_FACTION_ID)))
            ))
            .or()
            .condition(new NotExistsCondition(SqlBuilder.select()
                .column("1")
                .from(new QualifiedTable(SCHEMA, TABLE_MINOR_FACTION_CONFLICT))
                .condition(new Equation(new QualifiedColumn(TABLE_MINOR_FACTION_CONFLICT, COLUMN_ID), new QualifiedColumn(TABLE_CONFLICTING_MINOR_FACTION, COLUMN_CONFLICT_ID)))
            ))
            .build();

        return jdbcTemplate.update(sql);
    }
}
