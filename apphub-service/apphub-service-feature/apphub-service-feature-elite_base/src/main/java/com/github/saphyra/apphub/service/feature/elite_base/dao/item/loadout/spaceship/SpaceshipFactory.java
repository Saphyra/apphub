package com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SpaceshipFactory {
    public Spaceship create(ItemLocationType locationType, UUID externalReference, Long marketId, String name, UUID starSystemId) {
        return Spaceship.builder()
            .externalReference(externalReference)
            .locationType(locationType)
            .marketId(marketId)
            .itemName(name)
            .starSystemId(starSystemId)
            .build();
    }
}
