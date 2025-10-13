package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

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
class CommodityOrphanedRecordCleaner extends OrphanedRecordCleaner {
    private final JdbcTemplate jdbcTemplate;

    public CommodityOrphanedRecordCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate) {
        super(errorReporterService);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Orphanage getOrphanage() {
        return Orphanage.COMMODITY;
    }

    @Override
    public List<Orphanage> getPreconditions() {
        return List.of(Orphanage.STATION);
    }

    @Override
    protected int doCleanup() {
        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_COMMODITY))
            .condition(new NotExistsCondition(SqlBuilder.select()
                .column("1")
                .from(new QualifiedTable(SCHEMA, TABLE_STATION))
                .condition(new Equation(new QualifiedColumn(TABLE_STATION, COLUMN_ID), new QualifiedColumn(TABLE_COMMODITY, COLUMN_EXTERNAL_REFERENCE)))
                .or()
                .condition(new Equation(new QualifiedColumn(TABLE_STATION, COLUMN_MARKET_ID), new QualifiedColumn(TABLE_COMMODITY, COLUMN_MARKET_ID)))
            ))
            .and()
            .condition(new NotExistsCondition(SqlBuilder.select()
                .column("1")
                .from(new QualifiedTable(SCHEMA, TABLE_FLEET_CARRIER))
                .condition(new Equation(new QualifiedColumn(TABLE_FLEET_CARRIER, COLUMN_ID), new QualifiedColumn(TABLE_COMMODITY, COLUMN_EXTERNAL_REFERENCE)))
                .or()
                .condition(new Equation(new QualifiedColumn(TABLE_FLEET_CARRIER, COLUMN_MARKET_ID), new QualifiedColumn(TABLE_COMMODITY, COLUMN_MARKET_ID)))
            ))
            .build();

        return jdbcTemplate.update(sql);
    }
}
