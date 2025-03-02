package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommodityDataTransformerTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 343L;
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer BUY_PRICE = 243;
    private static final Integer SELL_PRICE = 457;
    private static final Integer DEMAND = 6576;
    private static final Integer STOCK = 758;
    private static final Integer AVERAGE_PRICE = 67;

    @Mock
    private CommodityFactory commodityFactory;

    @InjectMocks
    private CommodityDataTransformer underTest;

    @Mock
    private CommoditySaver.CommodityData commodityData;

    @Mock
    private Commodity existingCommodity;

    @Mock
    private Commodity transformedCommodity;

    @BeforeEach
    void setUp() {
        given(commodityData.getName()).willReturn(COMMODITY_NAME);
        given(commodityData.getBuyPrice()).willReturn(BUY_PRICE);
        given(commodityData.getSellPrice()).willReturn(SELL_PRICE);
        given(commodityData.getDemand()).willReturn(DEMAND);
        given(commodityData.getStock()).willReturn(STOCK);
        given(commodityData.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(commodityFactory.create(LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, COMMODITY_NAME, BUY_PRICE, SELL_PRICE, DEMAND, STOCK, AVERAGE_PRICE)).willReturn(transformedCommodity);
    }

    @Test
    void nullStored() {
        assertThat(underTest.transform(null, LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, commodityData)).contains(transformedCommodity);
    }

    @Test
    void oldMessage() {
        given(existingCommodity.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        assertThat(underTest.transform(existingCommodity, LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, commodityData)).isEmpty();
    }

    @Test
    void sameRecords() {
        given(commodityFactory.create(LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, COMMODITY_NAME, BUY_PRICE, SELL_PRICE, DEMAND, STOCK, AVERAGE_PRICE)).willReturn(existingCommodity);
        given(existingCommodity.getLastUpdate()).willReturn(LAST_UPDATE);

        assertThat(underTest.transform(existingCommodity, LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, commodityData)).isEmpty();
    }

    @Test
    void recordModified() {
        given(commodityFactory.create(LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, COMMODITY_NAME, BUY_PRICE, SELL_PRICE, DEMAND, STOCK, AVERAGE_PRICE)).willReturn(transformedCommodity);
        given(existingCommodity.getLastUpdate()).willReturn(LAST_UPDATE);

        assertThat(underTest.transform(existingCommodity, LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, commodityData)).contains(transformedCommodity);
    }
}