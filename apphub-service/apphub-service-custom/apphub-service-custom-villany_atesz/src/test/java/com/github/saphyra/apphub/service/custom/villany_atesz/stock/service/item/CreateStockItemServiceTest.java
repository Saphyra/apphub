package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateStockItemRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPrice;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price.StockItemPriceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateStockItemServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer PRICE = 35;

    @Mock
    private CreateStockItemRequestValidator createStockItemRequestValidator;

    @Mock
    private StockItemFactory stockItemFactory;

    @Mock
    private StockItemDao stockItemDao;

    @Mock
    private StockItemPriceFactory stockItemPriceFactory;

    @Mock
    private StockItemPriceDao stockItemPriceDao;

    @InjectMocks
    private CreateStockItemService underTest;

    @Mock
    private CreateStockItemRequest request;

    @Mock
    private StockItem stockItem;

    @Mock
    private StockItemPrice stockItemPrice;

    @Test
    void create() {
        given(stockItemFactory.create(USER_ID, request)).willReturn(stockItem);
        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(request.getPrice()).willReturn(PRICE);
        given(stockItemPriceFactory.create(USER_ID, STOCK_ITEM_ID, PRICE)).willReturn(stockItemPrice);

        underTest.create(USER_ID, request);

        then(createStockItemRequestValidator).should().validate(request);
        then(stockItemDao).should().save(stockItem);
        then(stockItemPriceDao).should().save(stockItemPrice);
    }
}