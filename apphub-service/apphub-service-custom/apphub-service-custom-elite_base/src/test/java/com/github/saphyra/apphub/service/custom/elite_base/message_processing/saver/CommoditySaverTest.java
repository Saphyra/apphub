package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.Tradeable;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.TradingDaoSupport;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.type.ItemTypeDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommoditySaverTest {
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 243L;
    private static final String EXISTING_COMMODITY_NAME = "existing-commodity-name";
    private static final String NEW_COMMODITY_NAME = "new-commodity-name";
    private static final String MODIFIED_COMMODITY_NAME = "modified-commodity-name";
    private static final Integer AVERAGE_PRICE = 24;
    private static final String DELETED_COMMODITY_NAME = "deleted-commodity-name";

    @Mock
    private TradingDaoSupport tradingDaoSupport;

    @Mock
    private CommodityDataTransformer commodityDataTransformer;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @Mock
    private LastUpdateFactory lastUpdateFactory;

    @Mock
    private PerformanceReporter performanceReporter;

    @Mock
    private CommodityAveragePriceSaver commodityAveragePriceSaver;

    @Mock
    private ItemTypeDao itemTypeDao;

    @InjectMocks
    private CommoditySaver underTest;

    @Mock
    private CommoditySaver.CommodityData existingCommodityData;

    @Mock
    private CommoditySaver.CommodityData modifiedCommodityData;

    @Mock
    private CommoditySaver.CommodityData newCommodityData;

    @Mock
    private CommoditySaver.CommodityData emptyCommodityData;

    @Mock
    private LastUpdate originalLastUpdate;

    @Mock
    private LastUpdate newLastUpdate;

    @Mock
    private Tradeable existingCommodity;

    @Mock
    private Tradeable corruptedCommodity;

    @Mock
    private Tradeable modifiedCommodity;

    @Mock
    private Tradeable newCommodity;

    @Mock
    private Tradeable deletedCommodity;

    @Test
    void nullMarketId() {
        assertThat(catchThrowable(() -> underTest.saveAll(TIMESTAMP, ItemType.COMMODITY, ItemLocationType.STATION, EXTERNAL_REFERENCE, null, List.of(existingCommodityData))))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyCommodity() {
        given(lastUpdateDao.findById(EXTERNAL_REFERENCE, ItemType.COMMODITY)).willReturn(Optional.of(originalLastUpdate));
        given(lastUpdateFactory.create(EXTERNAL_REFERENCE, ItemType.COMMODITY, TIMESTAMP)).willReturn(newLastUpdate);
        given(tradingDaoSupport.getByMarketId(ItemType.COMMODITY, MARKET_ID)).willReturn(List.of(existingCommodity, modifiedCommodity, corruptedCommodity, deletedCommodity));

        given(existingCommodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(modifiedCommodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(deletedCommodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(corruptedCommodity.getExternalReference()).willReturn(UUID.randomUUID());

        given(existingCommodity.getItemName()).willReturn(EXISTING_COMMODITY_NAME);
        given(modifiedCommodity.getItemName()).willReturn(MODIFIED_COMMODITY_NAME);
        given(deletedCommodity.getItemName()).willReturn(DELETED_COMMODITY_NAME);

        given(emptyCommodityData.getStock()).willReturn(0);
        given(emptyCommodityData.getDemand()).willReturn(0);
        given(newCommodityData.getStock()).willReturn(1);
        given(existingCommodityData.getStock()).willReturn(1);
        given(modifiedCommodityData.getStock()).willReturn(1);

        given(existingCommodityData.getName()).willReturn(EXISTING_COMMODITY_NAME);
        given(newCommodityData.getName()).willReturn(NEW_COMMODITY_NAME);
        given(modifiedCommodityData.getName()).willReturn(MODIFIED_COMMODITY_NAME);

        given(existingCommodityData.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(newCommodityData.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(modifiedCommodityData.getAveragePrice()).willReturn(AVERAGE_PRICE);

        given(commodityDataTransformer.transform(null, TIMESTAMP, ItemType.COMMODITY, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, newCommodityData, originalLastUpdate))
            .willReturn(Optional.of(newCommodity));
        given(commodityDataTransformer.transform(existingCommodity, TIMESTAMP, ItemType.COMMODITY, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, existingCommodityData, originalLastUpdate))
            .willReturn(Optional.empty());
        given(commodityDataTransformer.transform(modifiedCommodity, TIMESTAMP, ItemType.COMMODITY, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, modifiedCommodityData, originalLastUpdate))
            .willReturn(Optional.of(modifiedCommodity));

        underTest.saveAll(
            TIMESTAMP,
            ItemType.COMMODITY,
            ItemLocationType.STATION,
            EXTERNAL_REFERENCE,
            MARKET_ID,
            List.of(existingCommodityData, newCommodityData, emptyCommodityData, modifiedCommodityData)
        );

        then(lastUpdateDao).should().save(newLastUpdate);
        then(tradingDaoSupport).should().deleteAll(ItemType.COMMODITY, List.of(corruptedCommodity));
        then(commodityAveragePriceSaver).should().saveAveragePrices(TIMESTAMP, Map.of(EXISTING_COMMODITY_NAME, AVERAGE_PRICE, NEW_COMMODITY_NAME, AVERAGE_PRICE, MODIFIED_COMMODITY_NAME, AVERAGE_PRICE));
        then(itemTypeDao).should().saveAll(ItemType.COMMODITY, List.of(EXISTING_COMMODITY_NAME, NEW_COMMODITY_NAME, MODIFIED_COMMODITY_NAME));
        then(tradingDaoSupport).should().deleteAll(ItemType.COMMODITY, List.of(deletedCommodity));
        then(tradingDaoSupport).should().saveAll(ItemType.COMMODITY, List.of(newCommodity, modifiedCommodity));
    }
}