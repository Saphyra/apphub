package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Orphanage;
import com.github.saphyra.apphub.service.custom.elite_base.dao.OrphanedRecordCleaner;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.InCondition;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.ListValue;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedTable;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_LOADOUT;

@Component
@Slf4j
class LoadoutOrphanedRecordCleaner extends OrphanedRecordCleaner {
    private static final String QUERY_SQL = "select loadout.external_reference from elite_base.loadout" +
        " left join elite_base.station on loadout.external_reference = station.id or loadout.market_id = station.market_id" +
        " left join elite_base.fleet_carrier on loadout.external_reference = fleet_carrier.id or loadout.market_id = fleet_carrier.market_id" +
        " where station.id is null and station.market_id is null and fleet_carrier.id is null and fleet_carrier.market_id is null" +
        " group by loadout.external_reference" +
        " limit 10000";

    private final JdbcTemplate jdbcTemplate;

    public LoadoutOrphanedRecordCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate) {
        super(errorReporterService);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Orphanage getOrphanage() {
        return Orphanage.LOADOUT;
    }

    @Override
    public List<Orphanage> getPreconditions() {
        return List.of(Orphanage.STATION);
    }

    @Override
    protected int doCleanup() {
        List<String> toDelete = jdbcTemplate.query(
            QUERY_SQL, rs -> {
                List<String> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(rs.getString(COLUMN_EXTERNAL_REFERENCE));
                }

                return result;
            }
        );

        if (toDelete.isEmpty()) {
            log.debug("No orphaned loadouts to delete");
            return 0;
        }

        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_LOADOUT))
            .condition(new InCondition(new QualifiedColumn(TABLE_LOADOUT, COLUMN_EXTERNAL_REFERENCE), new ListValue(toDelete)))
            .build();

        return jdbcTemplate.update(sql);
    }
}
