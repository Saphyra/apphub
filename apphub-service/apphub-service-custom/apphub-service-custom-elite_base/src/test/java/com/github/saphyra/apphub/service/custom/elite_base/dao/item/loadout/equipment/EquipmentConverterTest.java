package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment;

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
class EquipmentConverterTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String ITEM_NAME = "item_name";
    private static final Long MARKET_ID = 123L;
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";

    @Mock
    private UuidConverter uuidConverter;

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
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(EXTERNAL_REFERENCE_STRING, equipmentEntity -> equipmentEntity.getId().getExternalReference())
            .returns(ITEM_NAME, equipmentEntity -> equipmentEntity.getId().getItemName())
            .returns(ItemLocationType.STATION, EquipmentEntity::getLocationType)
            .returns(MARKET_ID, EquipmentEntity::getMarketId)
            .returns(STAR_SYSTEM_ID_STRING, EquipmentEntity::getStarSystemId);
    }

    @Test
    void convertEntity() {
        EquipmentEntity entity = EquipmentEntity.builder()
            .id(ItemEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_STRING)
                .itemName(ITEM_NAME)
                .build())
            .locationType(ItemLocationType.STATION)
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
    }
}