package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommodityConverterTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String ITEM_NAME = "item_name";
    private static final Long MARKET_ID = 314L;
    private static final int BUY_PRICE = 123;
    private static final int SELL_PRICE = 456;
    private static final int DEMAND = 789;
    private static final int STOCK = 1011;
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private CommodityConverter underTest;

    @Test
    void convertDomain() {
        Commodity domain = Commodity.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .locationType(ItemLocationType.STATION)
            .marketId(MARKET_ID)
            .buyPrice(BUY_PRICE)
            .sellPrice(SELL_PRICE)
            .demand(DEMAND)
            .stock(STOCK)
            .starSystemId(STAR_SYSTEM_ID)
            .build();

        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(EXTERNAL_REFERENCE_STRING, commodityEntity -> commodityEntity.getId().getExternalReference())
            .returns(ITEM_NAME, commodityEntity -> commodityEntity.getId().getItemName())
            .returns(ItemLocationType.STATION, CommodityEntity::getLocationType)
            .returns(MARKET_ID, CommodityEntity::getMarketId)
            .returns(BUY_PRICE, CommodityEntity::getBuyPrice)
            .returns(SELL_PRICE, CommodityEntity::getSellPrice)
            .returns(DEMAND, CommodityEntity::getDemand)
            .returns(STOCK, CommodityEntity::getStock)
            .returns(STAR_SYSTEM_ID_STRING, CommodityEntity::getStarSystemId);
    }

    @Test
    void convertEntity() {
        CommodityEntity entity = CommodityEntity.builder()
            .id(ItemEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_STRING)
                .itemName(ITEM_NAME)
                .build())
            .locationType(ItemLocationType.STATION)
            .marketId(MARKET_ID)
            .buyPrice(BUY_PRICE)
            .sellPrice(SELL_PRICE)
            .demand(DEMAND)
            .stock(STOCK)
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);
        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(EXTERNAL_REFERENCE, Commodity::getExternalReference)
            .returns(ITEM_NAME, Commodity::getItemName)
            .returns(ItemLocationType.STATION, Commodity::getLocationType)
            .returns(MARKET_ID, Commodity::getMarketId)
            .returns(BUY_PRICE, Commodity::getBuyPrice)
            .returns(SELL_PRICE, Commodity::getSellPrice)
            .returns(DEMAND, Commodity::getDemand)
            .returns(STOCK, Commodity::getStock)
            .returns(STAR_SYSTEM_ID, Commodity::getStarSystemId);
    }
}