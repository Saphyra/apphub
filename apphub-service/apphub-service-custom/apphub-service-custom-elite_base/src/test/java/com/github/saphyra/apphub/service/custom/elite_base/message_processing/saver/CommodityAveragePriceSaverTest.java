package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommodityAveragePriceSaverTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer AVERAGE_PRICE = 1234;

    @Mock
    private CommodityAveragePriceDao commodityAveragePriceDao;

    @Mock
    private CommodityAveragePriceFactory commodityAveragePriceFactory;

    @InjectMocks
    private CommodityAveragePriceSaver underTest;

    @Mock
    private CommodityAveragePrice createdCap;

    @Mock
    private CommodityAveragePrice existingCap;

    @Test
    void saveNew() {
        given(commodityAveragePriceFactory.create(CURRENT_TIME, COMMODITY_NAME, AVERAGE_PRICE)).willReturn(createdCap);
        given(commodityAveragePriceDao.findAllById(Set.of(COMMODITY_NAME))).willReturn(List.of());

        underTest.saveAveragePrices(CURRENT_TIME, Map.of(COMMODITY_NAME, AVERAGE_PRICE));

        then(commodityAveragePriceDao).should().saveAll(List.of(createdCap));
    }

    @Test
    void sameAsCurrent() {
        given(commodityAveragePriceFactory.create(CURRENT_TIME, COMMODITY_NAME, AVERAGE_PRICE)).willReturn(createdCap);
        given(commodityAveragePriceDao.findAllById(Set.of(COMMODITY_NAME))).willReturn(List.of(existingCap));
        given(existingCap.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(createdCap.getAveragePrice()).willReturn(AVERAGE_PRICE);

        underTest.saveAveragePrices(CURRENT_TIME, Map.of(COMMODITY_NAME, AVERAGE_PRICE));

        then(commodityAveragePriceDao).should().saveAll(List.of());
    }

    @Test
    void olderEntry() {
        given(commodityAveragePriceFactory.create(CURRENT_TIME, COMMODITY_NAME, AVERAGE_PRICE)).willReturn(createdCap);
        given(commodityAveragePriceDao.findAllById(Set.of(COMMODITY_NAME))).willReturn(List.of(existingCap));
        given(existingCap.getAveragePrice()).willReturn(AVERAGE_PRICE + 1);
        given(createdCap.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(existingCap.getLastUpdate()).willReturn(CURRENT_TIME.plusMinutes(1));

        underTest.saveAveragePrices(CURRENT_TIME, Map.of(COMMODITY_NAME, AVERAGE_PRICE));

        then(commodityAveragePriceDao).should().saveAll(List.of());
    }

    @Test
    void overwriteExisting() {
        given(commodityAveragePriceFactory.create(CURRENT_TIME, COMMODITY_NAME, AVERAGE_PRICE)).willReturn(createdCap);
        given(commodityAveragePriceDao.findAllById(Set.of(COMMODITY_NAME))).willReturn(List.of(existingCap));
        given(existingCap.getAveragePrice()).willReturn(AVERAGE_PRICE + 1);
        given(createdCap.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(existingCap.getLastUpdate()).willReturn(CURRENT_TIME.minusMinutes(1));

        underTest.saveAveragePrices(CURRENT_TIME, Map.of(COMMODITY_NAME, AVERAGE_PRICE));

        then(commodityAveragePriceDao).should().saveAll(List.of(createdCap));
    }
}