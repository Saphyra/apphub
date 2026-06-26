package com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.equipment;

import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EquipmentFactoryTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 123L;
    private static final String NAME = "name";
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @InjectMocks
    private EquipmentFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, STAR_SYSTEM_ID))
            .returns(ItemLocationType.STATION, Equipment::getLocationType)
            .returns(EXTERNAL_REFERENCE, Equipment::getExternalReference)
            .returns(MARKET_ID, Equipment::getMarketId)
            .returns(NAME, Equipment::getItemName)
            .returns(STAR_SYSTEM_ID, Equipment::getStarSystemId);
    }
}