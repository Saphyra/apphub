package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.fc_material;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemIdAssembler;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemLocationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FcMaterialConverterTest {
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

    @Mock
    private ItemIdAssembler itemIdAssembler;

    @InjectMocks
    private FcMaterialConverter underTest;

    @Test
    void convertDomain() {
        FcMaterial domain = FcMaterial.builder()
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
        given(uuidConverter.convertDomain((UUID) null)).willReturn(null);
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(itemIdAssembler.assembleId(domain)).willReturn(ITEM_NAME + ":" + EXTERNAL_REFERENCE_STRING + ":");

        assertThat(underTest.convertDomain(domain))
            .returns(ITEM_NAME + ":" + EXTERNAL_REFERENCE_STRING + ":", FcMaterialEntity::getId)
            .returns(ITEM_NAME, FcMaterialEntity::getItemName)
            .returns(EXTERNAL_REFERENCE_STRING, FcMaterialEntity::getStationId)
            .returns(null, FcMaterialEntity::getFleetCarrierId)
            .returns(true, fcMaterialEntity -> fcMaterialEntity.getId() != null)
            .returns(MARKET_ID, FcMaterialEntity::getMarketId)
            .returns(BUY_PRICE, FcMaterialEntity::getBuyPrice)
            .returns(SELL_PRICE, FcMaterialEntity::getSellPrice)
            .returns(DEMAND, FcMaterialEntity::getDemand)
            .returns(STOCK, FcMaterialEntity::getStock)
            .returns(STAR_SYSTEM_ID_STRING, FcMaterialEntity::getStarSystemId);

        FcMaterial fleetCarrierDomain = FcMaterial.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .locationType(ItemLocationType.FLEET_CARRIER)
            .marketId(MARKET_ID)
            .buyPrice(BUY_PRICE)
            .sellPrice(SELL_PRICE)
            .demand(DEMAND)
            .stock(STOCK)
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        given(itemIdAssembler.assembleId(fleetCarrierDomain)).willReturn(ITEM_NAME + "::" + EXTERNAL_REFERENCE_STRING);
        assertThat(underTest.convertDomain(fleetCarrierDomain))
            .returns(ITEM_NAME + "::" + EXTERNAL_REFERENCE_STRING, FcMaterialEntity::getId)
            .returns(EXTERNAL_REFERENCE_STRING, FcMaterialEntity::getFleetCarrierId)
            .returns(null, FcMaterialEntity::getStationId);
    }

    @Test
    void convertEntity() {
        FcMaterialEntity entity = FcMaterialEntity.builder()
            .id("previous-id")
            .itemName(ITEM_NAME)
            .stationId(EXTERNAL_REFERENCE_STRING)
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
            .returns(EXTERNAL_REFERENCE, FcMaterial::getExternalReference)
            .returns(ITEM_NAME, FcMaterial::getItemName)
            .returns(ItemLocationType.STATION, FcMaterial::getLocationType)
            .returns(MARKET_ID, FcMaterial::getMarketId)
            .returns(BUY_PRICE, FcMaterial::getBuyPrice)
            .returns(SELL_PRICE, FcMaterial::getSellPrice)
            .returns(DEMAND, FcMaterial::getDemand)
            .returns(STOCK, FcMaterial::getStock)
            .returns(STAR_SYSTEM_ID, FcMaterial::getStarSystemId);

        FcMaterialEntity fleetCarrierEntity = FcMaterialEntity.builder()
            .id("previous-id")
            .itemName(ITEM_NAME)
            .fleetCarrierId(EXTERNAL_REFERENCE_STRING)
            .marketId(MARKET_ID)
            .buyPrice(BUY_PRICE)
            .sellPrice(SELL_PRICE)
            .demand(DEMAND)
            .stock(STOCK)
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .build();
        assertThat(underTest.convertEntity(fleetCarrierEntity))
            .returns(EXTERNAL_REFERENCE, FcMaterial::getExternalReference)
            .returns(ItemLocationType.FLEET_CARRIER, FcMaterial::getLocationType);
    }
}