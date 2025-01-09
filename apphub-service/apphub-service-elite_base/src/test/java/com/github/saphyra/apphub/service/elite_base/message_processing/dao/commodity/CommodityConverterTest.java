package com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.avg_price.CommodityAveragePriceFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.EntityType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommodityConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 32143L;
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer BUY_PRICE = 132;
    private static final Integer SELL_PRICE = 314;
    private static final Integer DEMAND = 6;
    private static final Integer STOCK = 56;
    private static final Integer AVERAGE_PRICE = 546;
    private static final String ID_STRING = "id";
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CommodityAveragePriceDao commodityAveragePriceDao;

    @Mock
    private CommodityAveragePriceFactory commodityAveragePriceFactory;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @InjectMocks
    private CommodityConverter underTest;

    @Mock
    private CommodityAveragePrice averagePrice;

    @Test
    void convertDomain() {
        Commodity domain = Commodity.builder()
            .id(ID)
            .lastUpdate(LAST_UPDATE)
            .type(CommodityType.COMMODITY)
            .commodityLocation(CommodityLocation.FLEET_CARRIER)
            .externalReference(EXTERNAL_REFERENCE)
            .marketId(MARKET_ID)
            .commodityName(COMMODITY_NAME)
            .buyPrice(BUY_PRICE)
            .sellPrice(SELL_PRICE)
            .demand(DEMAND)
            .stock(STOCK)
            .averagePrice(AVERAGE_PRICE)
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(commodityAveragePriceFactory.create(LAST_UPDATE, COMMODITY_NAME, AVERAGE_PRICE)).willReturn(averagePrice);

        assertThat(underTest.convertDomain(domain))
            .returns(ID_STRING, CommodityEntity::getId)
            .returns(CommodityType.COMMODITY, CommodityEntity::getType)
            .returns(CommodityLocation.FLEET_CARRIER, CommodityEntity::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE_STRING, CommodityEntity::getExternalReference)
            .returns(MARKET_ID, CommodityEntity::getMarketId)
            .returns(COMMODITY_NAME, CommodityEntity::getCommodityName)
            .returns(BUY_PRICE, CommodityEntity::getBuyPrice)
            .returns(SELL_PRICE, CommodityEntity::getSellPrice)
            .returns(STOCK, CommodityEntity::getStock)
            .returns(DEMAND, CommodityEntity::getDemand);

        then(commodityAveragePriceDao).should().save(averagePrice);
    }

    @Test
    void convertEntity() {
        CommodityEntity entity = CommodityEntity.builder()
            .id(ID_STRING)
            .type(CommodityType.COMMODITY)
            .commodityLocation(CommodityLocation.FLEET_CARRIER)
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .marketId(MARKET_ID)
            .commodityName(COMMODITY_NAME)
            .buyPrice(BUY_PRICE)
            .sellPrice(SELL_PRICE)
            .demand(DEMAND)
            .stock(STOCK)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);
        given(lastUpdateDao.findById(LastUpdateId.builder().externalReference(EXTERNAL_REFERENCE_STRING).type(EntityType.COMMODITY).build())).willReturn(Optional.of(LastUpdate.builder().lastUpdate(LAST_UPDATE).build()));
        given(commodityAveragePriceDao.findById(COMMODITY_NAME)).willReturn(Optional.of(averagePrice));
        given(averagePrice.getAveragePrice()).willReturn(AVERAGE_PRICE);

        assertThat(underTest.convertEntity(entity))
            .returns(ID, Commodity::getId)
            .returns(CommodityType.COMMODITY, Commodity::getType)
            .returns(CommodityLocation.FLEET_CARRIER, Commodity::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE, Commodity::getExternalReference)
            .returns(MARKET_ID, Commodity::getMarketId)
            .returns(COMMODITY_NAME, Commodity::getCommodityName)
            .returns(BUY_PRICE, Commodity::getBuyPrice)
            .returns(SELL_PRICE, Commodity::getSellPrice)
            .returns(STOCK, Commodity::getStock)
            .returns(DEMAND, Commodity::getDemand);
    }
}