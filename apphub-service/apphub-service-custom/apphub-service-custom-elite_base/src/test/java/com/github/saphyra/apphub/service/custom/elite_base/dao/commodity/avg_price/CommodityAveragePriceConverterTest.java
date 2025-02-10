package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price.CommodityAveragePriceConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price.CommodityAveragePriceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommodityAveragePriceConverterTest {
    private static final String COMMODITY_NAME = "commodity-name";
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Integer AVERAGE_PRICE = 324;
    private static final String LAST_UPDATE_STRING = "last-update";

    @Mock
    private DateTimeConverter dateTimeConverter;

    @InjectMocks
    private CommodityAveragePriceConverter underTest;

    @Test
    void convertDomain() {
        CommodityAveragePrice domain = CommodityAveragePrice.builder()
            .commodityName(COMMODITY_NAME)
            .lastUpdate(LAST_UPDATE)
            .averagePrice(AVERAGE_PRICE)
            .build();

        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(COMMODITY_NAME, CommodityAveragePriceEntity::getCommodityName)
            .returns(LAST_UPDATE_STRING, CommodityAveragePriceEntity::getLastUpdate)
            .returns(AVERAGE_PRICE, CommodityAveragePriceEntity::getAveragePrice);
    }

    @Test
    void convertEntity() {
        CommodityAveragePriceEntity domain = CommodityAveragePriceEntity.builder()
            .commodityName(COMMODITY_NAME)
            .lastUpdate(LAST_UPDATE_STRING)
            .averagePrice(AVERAGE_PRICE)
            .build();

        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);

        assertThat(underTest.convertEntity(domain))
            .returns(COMMODITY_NAME, CommodityAveragePrice::getCommodityName)
            .returns(LAST_UPDATE, CommodityAveragePrice::getLastUpdate)
            .returns(AVERAGE_PRICE, CommodityAveragePrice::getAveragePrice);
    }
}