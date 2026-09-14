package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CommodityAveragePriceSaverTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer AVERAGE_PRICE = 1234;

    @Mock
    private CommodityAveragePriceDao commodityAveragePriceDao;

    @Mock
    private CommodityAveragePriceFactory commodityAveragePriceFactory;

    @Mock
    private LastUpdateFactory lastUpdateFactory;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @InjectMocks
    private CommodityAveragePriceSaver underTest;

    @Mock
    private CommodityAveragePrice createdCap;

    @Mock
    private CommodityAveragePrice existingCap;

    @Mock
    private LastUpdate lastUpdate;

    @Test
    void saveNew() {
        given(commodityAveragePriceFactory.create(COMMODITY_NAME, AVERAGE_PRICE)).willReturn(createdCap);
        given(commodityAveragePriceDao.findAllById(Set.of(COMMODITY_NAME))).willReturn(List.of());
        given(lastUpdateFactory.create(COMMODITY_NAME, ObjectType.COMMODITY_AVERAGE_PRICE, CURRENT_TIME)).willReturn(lastUpdate);
        given(lastUpdateDao.findByIdOrDefault(COMMODITY_NAME, ObjectType.COMMODITY_AVERAGE_PRICE)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(CURRENT_TIME.minusMinutes(1));
        given(createdCap.getCommodityName()).willReturn(COMMODITY_NAME);

        underTest.saveAveragePrices(CURRENT_TIME, Map.of(COMMODITY_NAME, AVERAGE_PRICE));

        then(commodityAveragePriceDao).should().saveAll(List.of(createdCap));
        then(lastUpdateDao).should().save(lastUpdate);
    }

    @Test
    void sameAsCurrent() {
        given(commodityAveragePriceFactory.create(COMMODITY_NAME, AVERAGE_PRICE)).willReturn(createdCap);
        given(commodityAveragePriceDao.findAllById(Set.of(COMMODITY_NAME))).willReturn(List.of(existingCap));
        given(existingCap.getCommodityName()).willReturn(COMMODITY_NAME);
        given(existingCap.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(createdCap.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(lastUpdateFactory.create(COMMODITY_NAME, ObjectType.COMMODITY_AVERAGE_PRICE, CURRENT_TIME)).willReturn(lastUpdate);
        given(lastUpdateDao.findByIdOrDefault(COMMODITY_NAME, ObjectType.COMMODITY_AVERAGE_PRICE)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(CURRENT_TIME.minusMinutes(1));
        given(createdCap.getCommodityName()).willReturn(COMMODITY_NAME);

        underTest.saveAveragePrices(CURRENT_TIME, Map.of(COMMODITY_NAME, AVERAGE_PRICE));

        then(commodityAveragePriceDao).should().saveAll(List.of());
        then(lastUpdateDao).should().save(lastUpdate);
    }

    @Test
    void olderEntry() {
        given(commodityAveragePriceFactory.create(COMMODITY_NAME, AVERAGE_PRICE)).willReturn(createdCap);
        given(commodityAveragePriceDao.findAllById(Set.of(COMMODITY_NAME))).willReturn(List.of(existingCap));
        given(createdCap.getCommodityName()).willReturn(COMMODITY_NAME);
        given(lastUpdateDao.findByIdOrDefault(COMMODITY_NAME, ObjectType.COMMODITY_AVERAGE_PRICE)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(CURRENT_TIME.plusMinutes(1));

        underTest.saveAveragePrices(CURRENT_TIME, Map.of(COMMODITY_NAME, AVERAGE_PRICE));

        then(commodityAveragePriceDao).should().saveAll(List.of());
        then(lastUpdateDao).should(never()).save(any());
    }

    @Test
    void overwriteExisting() {
        given(commodityAveragePriceFactory.create(COMMODITY_NAME, AVERAGE_PRICE)).willReturn(createdCap);
        given(commodityAveragePriceDao.findAllById(Set.of(COMMODITY_NAME))).willReturn(List.of(existingCap));
        given(existingCap.getAveragePrice()).willReturn(AVERAGE_PRICE + 1);
        given(existingCap.getCommodityName()).willReturn(COMMODITY_NAME);
        given(createdCap.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(createdCap.getCommodityName()).willReturn(COMMODITY_NAME);
        given(lastUpdateDao.findByIdOrDefault(COMMODITY_NAME, ObjectType.COMMODITY_AVERAGE_PRICE)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(CURRENT_TIME.minusMinutes(1));
        given(lastUpdateFactory.create(COMMODITY_NAME, ObjectType.COMMODITY_AVERAGE_PRICE, CURRENT_TIME)).willReturn(lastUpdate);

        underTest.saveAveragePrices(CURRENT_TIME, Map.of(COMMODITY_NAME, AVERAGE_PRICE));

        then(commodityAveragePriceDao).should().saveAll(List.of(createdCap));
        then(lastUpdateDao).should().save(lastUpdate);
    }
}