package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

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

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_COMMODITY_NAME;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_COMMODITY;

@Component
@Slf4j
class CommodityDeleteBuffer extends DeleteBuffer<CommodityDomainId> {
    private final JdbcTemplate jdbcTemplate;

    protected CommodityDeleteBuffer(DateTimeUtil dateTimeUtil, JdbcTemplate jdbcTemplate) {
        super(dateTimeUtil);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doSynchronize() {
        deleteAll(buffer);
    }

    //Has to be JDBC, JPA blocks the flow for some reason
    private void deleteAll(Collection<CommodityDomainId> domains) {
        List<UUID> externalReferences = domains.stream()
            .map(CommodityDomainId::getExternalReference)
            .distinct()
            .toList();
        List<String> commodityNames = domains.stream()
            .map(CommodityDomainId::getCommodityName)
            .distinct()
            .toList();

        log.debug("Deleting commodities by externalReferences {} and commodityNames {}", externalReferences, commodityNames);

        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_COMMODITY))
            .condition(new InCondition(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE), new ListValue(externalReferences)))
            .and()
            .condition(new InCondition(new DefaultColumn(COLUMN_COMMODITY_NAME), new ListValue(commodityNames)))
            .build();

        log.debug(sql);

        jdbcTemplate.update(sql);
    }
}
