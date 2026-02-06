package com.github.saphyra.apphub.service.custom.elite_base.dao.item.type;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemTypeConverterTest {
    private static final String ITEM_NAME = "item-name";

    @InjectMocks
    private ItemTypeConverter underTest;

    @Test
    void convertDomain() {
        ItemTypeDto domain = ItemTypeDto.builder()
            .itemName(ITEM_NAME)
            .type(ItemType.COMMODITY)
            .build();

        assertThat(underTest.convertDomain(domain))
            .returns(ITEM_NAME, ItemTypeEntity::getItemName)
            .returns(ItemType.COMMODITY, ItemTypeEntity::getType);
    }

    @Test
    void convertEntity() {
        ItemTypeEntity entity = ItemTypeEntity.builder()
            .itemName(ITEM_NAME)
            .type(ItemType.COMMODITY)
            .build();

        assertThat(underTest.convertEntity(entity))
            .returns(ITEM_NAME, ItemTypeDto::getItemName)
            .returns(ItemType.COMMODITY, ItemTypeDto::getType);
    }
}