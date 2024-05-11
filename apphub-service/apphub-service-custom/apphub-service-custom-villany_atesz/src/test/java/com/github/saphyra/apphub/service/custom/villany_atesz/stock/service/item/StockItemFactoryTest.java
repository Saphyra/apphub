package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateStockItemRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockItemFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String SERIAL_NUMBER = "serial-number";
    private static final Integer IN_CAR = 35;
    private static final Integer IN_STORAGE = 54;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private StockItemFactory underTest;

    @Test
    void create() {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(STOCK_CATEGORY_ID)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .build();

        given(idGenerator.randomUuid()).willReturn(STOCK_ITEM_ID);

        assertThat(underTest.create(USER_ID, request))
            .returns(STOCK_ITEM_ID, StockItem::getStockItemId)
            .returns(USER_ID, StockItem::getUserId)
            .returns(STOCK_CATEGORY_ID, StockItem::getStockCategoryId)
            .returns(NAME, StockItem::getName)
            .returns(SERIAL_NUMBER, StockItem::getSerialNumber)
            .returns(IN_CAR, StockItem::getInCar)
            .returns(IN_STORAGE, StockItem::getInStorage);
    }

}