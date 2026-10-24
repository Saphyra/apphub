package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.equipment;

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
class EquipmentConverterTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String ITEM_NAME = "item_name";
    private static final Long MARKET_ID = 123L;
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ItemIdAssembler itemIdAssembler;

    @InjectMocks
    private EquipmentConverter underTest;

    @Test
    void convertDomain() {
        Equipment domain = Equipment.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .locationType(ItemLocationType.STATION)
            .marketId(MARKET_ID)
            .starSystemId(STAR_SYSTEM_ID)
            .build();

        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(uuidConverter.convertDomain((UUID) null)).willReturn(null);
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(itemIdAssembler.assembleId(domain)).willReturn(ITEM_NAME + ":" + EXTERNAL_REFERENCE_STRING + ":");

        assertThat(underTest.convertDomain(domain))
            .returns(ITEM_NAME + ":" + EXTERNAL_REFERENCE_STRING + ":", EquipmentEntity::getId)
            .returns(ITEM_NAME, EquipmentEntity::getItemName)
            .returns(EXTERNAL_REFERENCE_STRING, EquipmentEntity::getStationId)
            .returns(null, EquipmentEntity::getFleetCarrierId)
            .returns(true, equipmentEntity -> equipmentEntity.getId() != null)
            .returns(MARKET_ID, EquipmentEntity::getMarketId)
            .returns(STAR_SYSTEM_ID_STRING, EquipmentEntity::getStarSystemId);

        Equipment fleetCarrierDomain = Equipment.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .locationType(ItemLocationType.FLEET_CARRIER)
            .marketId(MARKET_ID)
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        given(itemIdAssembler.assembleId(fleetCarrierDomain)).willReturn(ITEM_NAME + "::" + EXTERNAL_REFERENCE_STRING);
        assertThat(underTest.convertDomain(fleetCarrierDomain))
            .returns(ITEM_NAME + "::" + EXTERNAL_REFERENCE_STRING, EquipmentEntity::getId)
            .returns(EXTERNAL_REFERENCE_STRING, EquipmentEntity::getFleetCarrierId)
            .returns(null, EquipmentEntity::getStationId);
    }

    @Test
    void convertEntity() {
        EquipmentEntity entity = EquipmentEntity.builder()
            .id("previous-id")
            .itemName(ITEM_NAME)
            .stationId(EXTERNAL_REFERENCE_STRING)
            .marketId(MARKET_ID)
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);
        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(EXTERNAL_REFERENCE, Equipment::getExternalReference)
            .returns(ITEM_NAME, Equipment::getItemName)
            .returns(ItemLocationType.STATION, Equipment::getLocationType)
            .returns(MARKET_ID, Equipment::getMarketId)
            .returns(STAR_SYSTEM_ID, Equipment::getStarSystemId);

        EquipmentEntity fleetCarrierEntity = EquipmentEntity.builder()
            .id("previous-id")
            .itemName(ITEM_NAME)
            .fleetCarrierId(EXTERNAL_REFERENCE_STRING)
            .marketId(MARKET_ID)
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .build();
        assertThat(underTest.convertEntity(fleetCarrierEntity))
            .returns(EXTERNAL_REFERENCE, Equipment::getExternalReference)
            .returns(ItemLocationType.FLEET_CARRIER, Equipment::getLocationType);
    }
}