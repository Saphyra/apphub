package com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommodityAveragePriceFactoryTest {
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer AVERAGE_PRICE = 123;

    @InjectMocks
    private CommodityAveragePriceFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(COMMODITY_NAME, AVERAGE_PRICE))
            .returns(COMMODITY_NAME, CommodityAveragePrice::getCommodityName)
            .returns(AVERAGE_PRICE, CommodityAveragePrice::getAveragePrice);
    }
}