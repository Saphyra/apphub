package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
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
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_NAME;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_LOADOUT;

@Component
@Slf4j
class LoadoutDeleteBuffer extends DeleteBuffer<LoadoutDomainId> {
    private final JdbcTemplate jdbcTemplate;

    protected LoadoutDeleteBuffer(DateTimeUtil dateTimeUtil, JdbcTemplate jdbcTemplate) {
        super(dateTimeUtil);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doSynchronize() {
        deleteAll(buffer);
    }

    private void deleteAll(Collection<LoadoutDomainId> domains) {
        List<UUID> externalReferences = domains.stream()
            .map(LoadoutDomainId::getExternalReference)
            .toList();
        List<String> loadoutNames = domains.stream()
            .map(LoadoutDomainId::getName)
            .toList();
        List<String> types = domains.stream()
            .map(LoadoutDomainId::getType)
            .map(Enum::name)
            .toList();

        log.debug("Deleting loadouts by externalReferences {} and types {} and loadoutNames {}", externalReferences, types, loadoutNames);

        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_LOADOUT))
            .condition(new InCondition(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE), new ListValue(externalReferences)))
            .and()
            .condition(new InCondition(new DefaultColumn(COLUMN_NAME), new ListValue(loadoutNames)))
            .and()
            .condition(new InCondition(new DefaultColumn(COLUMN_TYPE), new ListValue(types)))
            .build();

        log.debug(sql);

        jdbcTemplate.update(sql);
    }
}
