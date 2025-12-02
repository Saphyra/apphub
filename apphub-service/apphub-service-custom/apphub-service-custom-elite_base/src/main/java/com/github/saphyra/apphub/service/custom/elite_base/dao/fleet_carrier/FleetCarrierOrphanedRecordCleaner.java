package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.sql_builder.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.DistinctColumn;
import com.github.saphyra.apphub.lib.sql_builder.InCondition;
import com.github.saphyra.apphub.lib.sql_builder.ListValue;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.dao.BatchOrphanedRecordCleaner;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Orphanage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_FLEET_CARRIER;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;

@Component
@Slf4j
public class FleetCarrierOrphanedRecordCleaner extends BatchOrphanedRecordCleaner {
    private final EliteBaseProperties eliteBaseProperties;
    private final JdbcTemplate jdbcTemplate;

    public FleetCarrierOrphanedRecordCleaner(ErrorReporterService errorReporterService, EliteBaseProperties eliteBaseProperties, JdbcTemplate jdbcTemplate) {
        super(errorReporterService, eliteBaseProperties);
        this.eliteBaseProperties = eliteBaseProperties;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Orphanage getOrphanage() {
        return Orphanage.FLEET_CARRIER;
    }

    @Override
    public List<Orphanage> getPreconditions() {
        return List.of();
    }

    @Override
    protected List<String> fetchIds() {
        String sql = SqlBuilder.select()
            .column(new DistinctColumn(new DefaultColumn(COLUMN_STAR_SYSTEM_ID)))
            .from(new QualifiedTable(SCHEMA, TABLE_FLEET_CARRIER))
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
            .from(new QualifiedTable(SCHEMA, TABLE_FLEET_CARRIER))
            .condition(new InCondition(new DefaultColumn(COLUMN_STAR_SYSTEM_ID), new ListValue(idsToDelete)))
            .build();

        jdbcTemplate.update(sql);
    }
}
