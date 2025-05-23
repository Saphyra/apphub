package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommodityFactoryTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 343L;
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer BUY_PRICE = 345;
    private static final Integer SELL_PRICE = 23456;
    private static final Integer STOCK = 546;
    private static final Integer DEMAND = 46;
    private static final Integer AVERAGE_PRICE = 345;

    @InjectMocks
    private CommodityFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, COMMODITY_NAME, BUY_PRICE, SELL_PRICE, DEMAND, STOCK, AVERAGE_PRICE))
            .returns(LAST_UPDATE, Commodity::getLastUpdate)
            .returns(CommodityType.COMMODITY, Commodity::getType)
            .returns(CommodityLocation.STATION, Commodity::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE, Commodity::getExternalReference)
            .returns(MARKET_ID, Commodity::getMarketId)
            .returns(COMMODITY_NAME, Commodity::getCommodityName)
            .returns(BUY_PRICE, Commodity::getBuyPrice)
            .returns(SELL_PRICE, Commodity::getSellPrice)
            .returns(DEMAND, Commodity::getDemand)
            .returns(STOCK, Commodity::getStock)
            .returns(AVERAGE_PRICE, Commodity::getAveragePrice);
    }
}