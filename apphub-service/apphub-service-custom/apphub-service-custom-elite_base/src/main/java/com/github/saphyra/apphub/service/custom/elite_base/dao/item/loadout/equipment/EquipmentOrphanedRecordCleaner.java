package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment;

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

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_FLEET_CARRIER;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_ITEM_EQUIPMENT;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STATION;

@Component
@Slf4j
class EquipmentOrphanedRecordCleaner extends BatchOrphanedRecordCleaner {
    private final JdbcTemplate jdbcTemplate;
    private final EliteBaseProperties eliteBaseProperties;

    EquipmentOrphanedRecordCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate, EliteBaseProperties eliteBaseProperties) {
        super(errorReporterService, eliteBaseProperties);
        this.jdbcTemplate = jdbcTemplate;
        this.eliteBaseProperties = eliteBaseProperties;
    }

    @Override
    public Orphanage getOrphanage() {
        return Orphanage.ITEM_EQUIPMENT;
    }

    @Override
    public List<Orphanage> getPreconditions() {
        return List.of(Orphanage.STATION, Orphanage.FLEET_CARRIER);
    }

    @Override
    protected List<String> fetchIds() {
        String sql = SqlBuilder.select()
            .column(new DistinctColumn(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE)))
            .from(new QualifiedTable(SCHEMA, TABLE_ITEM_EQUIPMENT))
            .except(SqlBuilder.select().column(COLUMN_ID).from(new QualifiedTable(SCHEMA, TABLE_STATION)))
            .except(SqlBuilder.select().column(COLUMN_ID).from(new QualifiedTable(SCHEMA, TABLE_FLEET_CARRIER)))
            .limit(eliteBaseProperties.getOrphanedRecordCleaner().getBatchSize())
            .build();

        return jdbcTemplate.query(sql, rs -> {
            List<String> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rs.getString(COLUMN_EXTERNAL_REFERENCE));
            }
            return result;
        });
    }

    @Override
    protected void delete(List<String> idsToDelete) {
        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_ITEM_EQUIPMENT))
            .condition(new InCondition(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE), new ListValue(idsToDelete)))
            .build();

        jdbcTemplate.update(sql);
    }
}
