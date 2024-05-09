package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPrice;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceDao;
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
public class StockItemPriceQueryServiceTest {
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final int PRICE = 53;

    @Mock
    private StockItemPriceDao stockItemPriceDao;

    @InjectMocks
    private StockItemPriceQueryService underTest;

    @Mock
    private StockItemPrice stockItemPrice;

    @Test
    void getPriceOf() {
        given(stockItemPriceDao.getByStockItemId(STOCK_ITEM_ID)).willReturn(List.of(stockItemPrice, stockItemPrice));
        given(stockItemPrice.getPrice())
            .willReturn(PRICE - 25)
            .willReturn(PRICE);

        assertThat(underTest.getPriceOf(STOCK_ITEM_ID)).isEqualTo(PRICE);
    }
}