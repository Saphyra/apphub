package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.Tradeable;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.TradingDaoSupport;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdate;
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
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 12345L;
    private static final String NAME = "name";
    private static final Integer BUY_PRICE = 100;
    private static final Integer SELL_PRICE = 150;
    private static final Integer DEMAND = 200;
    private static final Integer STOCK = 300;
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private TradingDaoSupport tradingDaoSupport;

    @InjectMocks
    private CommodityDataTransformer underTest;

    @Mock
    private Tradeable existing;

    @Mock
    private Tradeable created;

    @Mock
    private CommoditySaver.CommodityData commodityData;

    @Mock
    private LastUpdate originalLastUpdate;

    @BeforeEach
    void setUp() {
        given(commodityData.getName()).willReturn(NAME);
        given(commodityData.getBuyPrice()).willReturn(BUY_PRICE);
        given(commodityData.getSellPrice()).willReturn(SELL_PRICE);
        given(commodityData.getDemand()).willReturn(DEMAND);
        given(commodityData.getStock()).willReturn(STOCK);

        given(tradingDaoSupport.create(
            ItemType.COMMODITY,
            ItemLocationType.STATION,
            EXTERNAL_REFERENCE,
            MARKET_ID,
            NAME,
            BUY_PRICE,
            SELL_PRICE,
            DEMAND,
            STOCK
        )).willReturn(created);
    }

    @Test
    void noStored() {
        assertThat(underTest.transform(null, CURRENT_TIME, ItemType.COMMODITY, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, commodityData, originalLastUpdate)).contains(created);
    }

    @Test
    void outdatedData() {
        given(originalLastUpdate.getLastUpdate()).willReturn(CURRENT_TIME.plusMinutes(1));

        assertThat(underTest.transform(existing, CURRENT_TIME, ItemType.COMMODITY, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, commodityData, originalLastUpdate)).isEmpty();
    }

    @Test
    void dataNotModified() {
        given(originalLastUpdate.getLastUpdate()).willReturn(CURRENT_TIME.minusMinutes(1));
        given(tradingDaoSupport.create(
            ItemType.COMMODITY,
            ItemLocationType.STATION,
            EXTERNAL_REFERENCE,
            MARKET_ID,
            NAME,
            BUY_PRICE,
            SELL_PRICE,
            DEMAND,
            STOCK
        )).willReturn(existing);

        assertThat(underTest.transform(existing, CURRENT_TIME, ItemType.COMMODITY, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, commodityData, originalLastUpdate)).isEmpty();
    }

    @Test
    void newData() {
        given(originalLastUpdate.getLastUpdate()).willReturn(CURRENT_TIME.minusMinutes(1));

        assertThat(underTest.transform(existing, CURRENT_TIME, ItemType.COMMODITY, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, commodityData, originalLastUpdate)).contains(created);
    }
}