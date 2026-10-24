package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemIdAssembler;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemLocationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
class SpaceshipConverter extends ConverterBase<SpaceshipEntity, Spaceship> {
    private final UuidConverter uuidConverter;
    private final ItemIdAssembler itemIdAssembler;

    @Override
    protected SpaceshipEntity processDomainConversion(Spaceship domain) {
        return SpaceshipEntity.builder()
            .id(itemIdAssembler.assembleId(domain))
            .itemName(domain.getItemName())
            .stationId(uuidConverter.convertDomain(domain.getStationId()))
            .fleetCarrierId(uuidConverter.convertDomain(domain.getFleetCarrierId()))
            .marketId(domain.getMarketId())
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .build();
    }

    @Override
    protected Spaceship processEntityConversion(SpaceshipEntity entity) {
        UUID externalReference = null;
        ItemLocationType locationType = ItemLocationType.UNKNOWN;
        if (nonNull(entity.getStationId())) {
            externalReference = uuidConverter.convertEntity(entity.getStationId());
            locationType = ItemLocationType.STATION;
        } else if (nonNull(entity.getFleetCarrierId())) {
            externalReference = uuidConverter.convertEntity(entity.getFleetCarrierId());
            locationType = ItemLocationType.FLEET_CARRIER;
        }

        return Spaceship.builder()
            .externalReference(externalReference)
            .itemName(entity.getItemName())
            .locationType(locationType)
            .marketId(entity.getMarketId())
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .build();
    }
}
