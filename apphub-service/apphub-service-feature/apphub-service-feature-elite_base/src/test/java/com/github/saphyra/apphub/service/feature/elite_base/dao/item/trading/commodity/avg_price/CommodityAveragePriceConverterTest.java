package com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommodityAveragePriceConverterTest {
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer AVERAGE_PRICE = 324;

    @InjectMocks
    private CommodityAveragePriceConverter underTest;

    @Test
    void convertDomain() {
        CommodityAveragePrice domain = CommodityAveragePrice.builder()
            .commodityName(COMMODITY_NAME)
            .averagePrice(AVERAGE_PRICE)
            .build();

        assertThat(underTest.convertDomain(domain))
            .returns(COMMODITY_NAME, CommodityAveragePriceEntity::getCommodityName)
            .returns(AVERAGE_PRICE, CommodityAveragePriceEntity::getAveragePrice);
    }

    @Test
    void convertEntity() {
        CommodityAveragePriceEntity domain = CommodityAveragePriceEntity.builder()
            .commodityName(COMMODITY_NAME)
            .averagePrice(AVERAGE_PRICE)
            .build();

        assertThat(underTest.convertEntity(domain))
            .returns(COMMODITY_NAME, CommodityAveragePrice::getCommodityName)
            .returns(AVERAGE_PRICE, CommodityAveragePrice::getAveragePrice);
    }
}