package com.github.saphyra.apphub.service.custom.elite_base.dao.item.type;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.springframework.stereotype.Component;

@Component
class ItemTypeConverter extends ConverterBase<ItemTypeEntity, ItemTypeDto> {
    @Override
    protected ItemTypeEntity processDomainConversion(ItemTypeDto domain) {
        return ItemTypeEntity.builder()
            .itemName(domain.getItemName())
            .type(domain.getType())
            .build();
    }

    @Override
    protected ItemTypeDto processEntityConversion(ItemTypeEntity entity) {
        return ItemTypeDto.builder()
            .itemName(entity.getItemName())
            .type(entity.getType())
            .build();
    }
}
