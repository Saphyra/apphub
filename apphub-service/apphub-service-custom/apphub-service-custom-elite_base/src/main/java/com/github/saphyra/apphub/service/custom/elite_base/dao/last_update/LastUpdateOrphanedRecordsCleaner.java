package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

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
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_ITEM_COMMODITY;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_ITEM_EQUIPMENT;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_ITEM_FC_MATERIAL;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_ITEM_SPACESHIP;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE;

@Component
@Slf4j
class LastUpdateOrphanedRecordsCleaner extends BatchOrphanedRecordCleaner {
    private final JdbcTemplate jdbcTemplate;
    private final EliteBaseProperties eliteBaseProperties;

    LastUpdateOrphanedRecordsCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate, EliteBaseProperties eliteBaseProperties) {
        super(errorReporterService, eliteBaseProperties);
        this.jdbcTemplate = jdbcTemplate;
        this.eliteBaseProperties = eliteBaseProperties;
    }

    @Override
    public Orphanage getOrphanage() {
        return Orphanage.LAST_UPDATE;
    }

    @Override
    public List<Orphanage> getPreconditions() {
        return List.of(Orphanage.ITEM_COMMODITY, Orphanage.ITEM_EQUIPMENT, Orphanage.ITEM_FC_MATERIAL, Orphanage.ITEM_SPACESHIP);
    }

    @Override
    protected List<String> fetchIds() {
        String sql = SqlBuilder.select()
            .column(new DistinctColumn(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE)))
            .from(new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE))
            .except(SqlBuilder.select().column(new DistinctColumn(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE))).from(new QualifiedTable(SCHEMA, TABLE_ITEM_COMMODITY)))
            .except(SqlBuilder.select().column(new DistinctColumn(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE))).from(new QualifiedTable(SCHEMA, TABLE_ITEM_FC_MATERIAL)))
            .except(SqlBuilder.select().column(new DistinctColumn(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE))).from(new QualifiedTable(SCHEMA, TABLE_ITEM_EQUIPMENT)))
            .except(SqlBuilder.select().column(new DistinctColumn(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE))).from(new QualifiedTable(SCHEMA, TABLE_ITEM_SPACESHIP)))
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
            .from(new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE))
            .condition(new InCondition(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE), new ListValue(idsToDelete)))
            .build();

        jdbcTemplate.update(sql);
    }
}
