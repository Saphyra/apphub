package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
class EquipmentConverter extends ConverterBase<EquipmentEntity, Equipment> {
    private final UuidConverter uuidConverter;

    @Override
    protected EquipmentEntity processDomainConversion(Equipment domain) {
        return EquipmentEntity.builder()
            .id(ItemEntityId.builder()
                .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
                .itemName(domain.getItemName())
                .build())
            .locationType(domain.getLocationType())
            .marketId(domain.getMarketId())
            .build();
    }

    @Override
    protected Equipment processEntityConversion(EquipmentEntity entity) {
        return Equipment.builder()
            .externalReference(uuidConverter.convertEntity(entity.getId().getExternalReference()))
            .itemName(entity.getId().getItemName())
            .locationType(entity.getLocationType())
            .marketId(entity.getMarketId())
            .build();
    }
}
