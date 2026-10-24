package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.equipment;

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
class EquipmentConverter extends ConverterBase<EquipmentEntity, Equipment> {
    private final UuidConverter uuidConverter;
    private final ItemIdAssembler itemIdAssembler;

    @Override
    protected EquipmentEntity processDomainConversion(Equipment domain) {
        return EquipmentEntity.builder()
            .id(itemIdAssembler.assembleId(domain))
            .itemName(domain.getItemName())
            .stationId(uuidConverter.convertDomain(domain.getStationId()))
            .fleetCarrierId(uuidConverter.convertDomain(domain.getFleetCarrierId()))
            .marketId(domain.getMarketId())
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .build();
    }

    @Override
    protected Equipment processEntityConversion(EquipmentEntity entity) {
        UUID externalReference = null;
        ItemLocationType locationType = ItemLocationType.UNKNOWN;
        if (nonNull(entity.getStationId())) {
            externalReference = uuidConverter.convertEntity(entity.getStationId());
            locationType = ItemLocationType.STATION;
        } else if (nonNull(entity.getFleetCarrierId())) {
            externalReference = uuidConverter.convertEntity(entity.getFleetCarrierId());
            locationType = ItemLocationType.FLEET_CARRIER;
        }

        return Equipment.builder()
            .externalReference(externalReference)
            .itemName(entity.getItemName())
            .locationType(locationType)
            .marketId(entity.getMarketId())
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .build();
    }
}
