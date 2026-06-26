package com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.equipment;

import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EquipmentFactory {
    public Equipment create(ItemLocationType locationType, UUID externalReference, Long marketId, String name, UUID starSystemId) {
        return Equipment.builder()
            .locationType(locationType)
            .externalReference(externalReference)
            .marketId(marketId)
            .itemName(name)
            .starSystemId(starSystemId)
            .build();
    }
}
