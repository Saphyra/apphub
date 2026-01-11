package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//TODO unit test
public class SpaceshipFactory {
    public Spaceship create(ItemLocationType locationType, UUID externalReference, Long marketId, String name) {
        return Spaceship.builder()
            .externalReference(externalReference)
            .locationType(locationType)
            .marketId(marketId)
            .itemName(name)
            .build();
    }
}
