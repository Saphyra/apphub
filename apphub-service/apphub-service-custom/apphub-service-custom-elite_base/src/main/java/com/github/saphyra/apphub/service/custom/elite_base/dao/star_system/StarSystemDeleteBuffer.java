package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.DeleteBuffer;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.DefaultColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.InCondition;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.ListValue;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedTable;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SqlBuilder;
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
    private final StarSystemRepository starSystemRepository;
    private final UuidConverter uuidConverter;
    private final JdbcTemplate jdbcTemplate;

    StarSystemDeleteBuffer(DateTimeUtil dateTimeUtil, StarSystemRepository starSystemRepository, UuidConverter uuidConverter, JdbcTemplate jdbcTemplate) {
        super(dateTimeUtil);
        this.starSystemRepository = starSystemRepository;
        this.uuidConverter = uuidConverter;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doSynchronize() {
        deleteAll(buffer);
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
