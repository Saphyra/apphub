package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//TODO unit test
public class EquipmentFactory {
    public Equipment create(ItemLocationType locationType, UUID externalReference, Long marketId, String name) {
        return Equipment.builder()
            .locationType(locationType)
            .externalReference(externalReference)
            .marketId(marketId)
            .itemName(name)
            .build();
    }
}
