package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPrice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StockItemPriceFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_PRICE_ID = UUID.randomUUID();
    private static final Integer PRICE = 34;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private StockItemPriceFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(STOCK_ITEM_PRICE_ID);

        assertThat(underTest.create(USER_ID, STOCK_ITEM_ID, PRICE))
            .returns(STOCK_ITEM_PRICE_ID, StockItemPrice::getStockItemPriceId)
            .returns(STOCK_ITEM_ID, StockItemPrice::getStockItemId)
            .returns(USER_ID, StockItemPrice::getUserId)
            .returns(PRICE, StockItemPrice::getPrice);
    }
}