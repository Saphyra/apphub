package com.github.saphyra.apphub.service.custom.villany_atesz.index.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price.StockItemPriceQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockTotalValueQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final int IN_CAR = 23;
    private static final Integer IN_STORAGE = 452;
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer PRICE = 3546;

    @Mock
    private StockItemDao stockItemDao;

    @Mock
    private StockItemPriceQueryService stockItemPriceQueryService;

    @InjectMocks
    private StockTotalValueQueryService underTest;

    @Mock
    private StockItem stockItem;

    @Test
    void getTotalValue() {
        given(stockItemDao.getByUserId(USER_ID)).willReturn(List.of(stockItem));
        given(stockItem.getInCar()).willReturn(IN_CAR);
        given(stockItem.getInStorage()).willReturn(IN_STORAGE);
        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(stockItemPriceQueryService.getPriceOf(STOCK_ITEM_ID)).willReturn(PRICE);

        assertThat(underTest.getTotalValue(USER_ID)).isEqualTo((IN_CAR + IN_STORAGE) * PRICE);
    }
}