package com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_material;

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

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_BODY_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_BODY;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_BODY_MATERIAL;

@Component
@Slf4j
class BodyMaterialOrphanedRecordCleaner extends BatchOrphanedRecordCleaner {
    private final JdbcTemplate jdbcTemplate;
    private final EliteBaseProperties eliteBaseProperties;

    public BodyMaterialOrphanedRecordCleaner(ErrorReporterService errorReporterService, JdbcTemplate jdbcTemplate, EliteBaseProperties eliteBaseProperties) {
        super(errorReporterService, eliteBaseProperties);
        this.jdbcTemplate = jdbcTemplate;
        this.eliteBaseProperties = eliteBaseProperties;
    }

    @Override
    public Orphanage getOrphanage() {
        return Orphanage.BODY_MATERIAL;
    }

    @Override
    public List<Orphanage> getPreconditions() {
        return List.of(Orphanage.BODY);
    }

    @Override
    protected List<String> fetchIds() {
        String sql = SqlBuilder.select()
            .column(new DistinctColumn(new DefaultColumn(COLUMN_BODY_ID)))
            .from(new QualifiedTable(SCHEMA, TABLE_BODY_MATERIAL))
            .except(SqlBuilder.select().column(COLUMN_ID).from(new QualifiedTable(SCHEMA, TABLE_BODY)))
            .limit(eliteBaseProperties.getOrphanedRecordCleaner().getBatchSize())
            .build();

        return jdbcTemplate.query(sql, rs -> {
            List<String> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rs.getString(COLUMN_BODY_ID));
            }
            return result;
        });
    }

    @Override
    protected void delete(List<String> idsToDelete) {
        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_BODY_MATERIAL))
            .condition(new InCondition(new DefaultColumn(COLUMN_BODY_ID), new ListValue(idsToDelete)))
            .build();

        jdbcTemplate.update(sql);
    }
}
