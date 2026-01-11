package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
class SpaceshipConverter extends ConverterBase<SpaceshipEntity, Spaceship> {
    private final UuidConverter uuidConverter;

    @Override
    protected SpaceshipEntity processDomainConversion(Spaceship domain) {
        return SpaceshipEntity.builder()
            .id(ItemEntityId.builder()
                .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
                .itemName(domain.getItemName())
                .build())
            .locationType(domain.getLocationType())
            .marketId(domain.getMarketId())
            .build();
    }

    @Override
    protected Spaceship processEntityConversion(SpaceshipEntity entity) {
        return Spaceship.builder()
            .externalReference(uuidConverter.convertEntity(entity.getId().getExternalReference()))
            .itemName(entity.getId().getItemName())
            .locationType(entity.getLocationType())
            .marketId(entity.getMarketId())
            .build();
    }
}
