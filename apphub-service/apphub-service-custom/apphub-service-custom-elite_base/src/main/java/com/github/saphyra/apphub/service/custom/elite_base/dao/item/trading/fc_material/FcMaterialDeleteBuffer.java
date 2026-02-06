package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.DeleteBuffer;
import com.github.saphyra.apphub.lib.sql_builder.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.InCondition;
import com.github.saphyra.apphub.lib.sql_builder.ListValue;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_ITEM_NAME;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_ITEM_FC_MATERIAL;

@Component
@Slf4j
class FcMaterialDeleteBuffer extends DeleteBuffer<ItemDomainId> {
    private final JdbcTemplate jdbcTemplate;

    protected FcMaterialDeleteBuffer(DateTimeUtil dateTimeUtil, JdbcTemplate jdbcTemplate) {
        super(dateTimeUtil);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doSynchronize(Collection<ItemDomainId> domains) {
        List<UUID> externalReferences = domains.stream()
            .map(ItemDomainId::getExternalReference)
            .distinct()
            .toList();
        List<String> commodityNames = domains.stream()
            .map(ItemDomainId::getItemName)
            .distinct()
            .toList();

        log.debug("Deleting FcMaterials by externalReferences {} and commodityNames {}", externalReferences, commodityNames);

        String sql = SqlBuilder.delete()
            .from(new QualifiedTable(SCHEMA, TABLE_ITEM_FC_MATERIAL))
            .condition(new InCondition(new DefaultColumn(COLUMN_EXTERNAL_REFERENCE), new ListValue(externalReferences)))
            .and()
            .condition(new InCondition(new DefaultColumn(COLUMN_ITEM_NAME), new ListValue(commodityNames)))
            .build();

        log.debug(sql);

        jdbcTemplate.update(sql);
    }
}
