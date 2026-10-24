package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.fc_material;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.DeleteBuffer;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemDomainId;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemLocationType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_ITEM_FC_MATERIAL_V2;

@Component
class FcMaterialDeleteBuffer extends DeleteBuffer<ItemDomainId> {
    private final JdbcTemplate jdbcTemplate;

    protected FcMaterialDeleteBuffer(DateTimeUtil dateTimeUtil, JdbcTemplate jdbcTemplate) {
        super(dateTimeUtil);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doSynchronize(Collection<ItemDomainId> domains) {
        List<ItemDomainId> entityIds = domains.stream().filter(domain -> domain.getEntityId() != null).toList();
        List<ItemDomainId> stations = domains.stream().filter(domain -> domain.getEntityId() == null && domain.getLocationType() == ItemLocationType.STATION).toList();
        List<ItemDomainId> fleetCarriers = domains.stream().filter(domain -> domain.getEntityId() == null && domain.getLocationType() == ItemLocationType.FLEET_CARRIER).toList();
        List<ItemDomainId> unknowns = domains.stream().filter(domain -> domain.getEntityId() == null && domain.getLocationType() != ItemLocationType.STATION && domain.getLocationType() != ItemLocationType.FLEET_CARRIER).toList();

        batchDelete("DELETE FROM %s.%s WHERE id = ?".formatted(SCHEMA, TABLE_ITEM_FC_MATERIAL_V2), entityIds, domain -> new Object[]{domain.getEntityId()});
        batchDelete("DELETE FROM %s.%s WHERE station_id = ? AND item_name = ?".formatted(SCHEMA, TABLE_ITEM_FC_MATERIAL_V2), stations, domain -> new Object[]{domain.getExternalReference().toString(), domain.getItemName()});
        batchDelete("DELETE FROM %s.%s WHERE fleet_carrier_id = ? AND item_name = ?".formatted(SCHEMA, TABLE_ITEM_FC_MATERIAL_V2), fleetCarriers, domain -> new Object[]{domain.getExternalReference().toString(), domain.getItemName()});
        batchDelete("DELETE FROM %s.%s WHERE station_id IS NULL AND fleet_carrier_id IS NULL AND item_name = ?".formatted(SCHEMA, TABLE_ITEM_FC_MATERIAL_V2), unknowns, domain -> new Object[]{domain.getItemName()});
    }

    private void batchDelete(String sql, List<ItemDomainId> domains, Function<ItemDomainId, Object[]> parameters) {
        if (!domains.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, domains.stream().map(parameters).toList());
        }
    }
}
