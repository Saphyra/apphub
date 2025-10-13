package com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_ring;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Orphanage;
import com.github.saphyra.apphub.service.custom.elite_base.dao.OrphanedRecordCleaner;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.*;

@Component
@Slf4j
class BodyRingOrphanedRecordCleaner extends OrphanedRecordCleaner {
    private final JdbcTemplate jdbcTemplate;

    public BodyRingOrphanedRecordCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate) {
        super(errorReporterService);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Orphanage getOrphanage() {
        return Orphanage.BODY_RING;
    }

    @Override
    public List<Orphanage> getPreconditions() {
        return List.of(Orphanage.BODY);
    }

    @Override
    protected int doCleanup() {
        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_BODY_RING))
            .condition(new NotExistsCondition(SqlBuilder.select()
                .column("1")
                .from(new QualifiedTable(SCHEMA, TABLE_BODY))
                .condition(new Equation(new QualifiedColumn(TABLE_BODY, COLUMN_ID), new QualifiedColumn(TABLE_BODY_RING, COLUMN_BODY_ID)))
            ))
            .build();

        return jdbcTemplate.update(sql);
    }
}
