package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.DeleteBuffer;
import com.github.saphyra.apphub.lib.sql_builder.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.InCondition;
import com.github.saphyra.apphub.lib.sql_builder.ListValue;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;

@Component
@Slf4j
class StarSystemDeleteBuffer extends DeleteBuffer<UUID> {
    private final JdbcTemplate jdbcTemplate;

    StarSystemDeleteBuffer(DateTimeUtil dateTimeUtil, JdbcTemplate jdbcTemplate) {
        super(dateTimeUtil);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doSynchronize(Collection<UUID> bufferCopy) {
        deleteAll(bufferCopy);
    }

    private void deleteAll(Collection<UUID> buffer) {
        log.debug("Deleting StarSystems by id {}", buffer);

        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM))
            .condition(new InCondition(new DefaultColumn(COLUMN_ID), new ListValue(buffer)))
            .build();

        log.debug(sql);

        jdbcTemplate.update(sql);
    }
}
