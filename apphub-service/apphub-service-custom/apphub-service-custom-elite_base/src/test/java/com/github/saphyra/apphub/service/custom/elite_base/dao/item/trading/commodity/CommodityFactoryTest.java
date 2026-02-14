package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommodityFactoryTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 1L;
    private static final String NAME = "name";
    private static final Integer BUY_PRICE = 2;
    private static final Integer SELL_PRICE = 3;
    private static final Integer DEMAND = 4;
    private static final Integer STOCK = 5;

    @InjectMocks
    private CommodityFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, BUY_PRICE, SELL_PRICE, DEMAND, STOCK))
            .returns(ItemLocationType.STATION, Commodity::getLocationType)
            .returns(EXTERNAL_REFERENCE, Commodity::getExternalReference)
            .returns(MARKET_ID, Commodity::getMarketId)
            .returns(NAME, Commodity::getItemName)
            .returns(BUY_PRICE, Commodity::getBuyPrice)
            .returns(SELL_PRICE, Commodity::getSellPrice)
            .returns(DEMAND, Commodity::getDemand)
            .returns(STOCK, Commodity::getStock);
    }
}