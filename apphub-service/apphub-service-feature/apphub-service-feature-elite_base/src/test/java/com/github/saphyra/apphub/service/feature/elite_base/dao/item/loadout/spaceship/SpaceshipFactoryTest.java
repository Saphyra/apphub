package com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SpaceshipFactoryTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 123L;
    private static final String NAME = "name";
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @InjectMocks
    private SpaceshipFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, STAR_SYSTEM_ID))
            .returns(EXTERNAL_REFERENCE, Spaceship::getExternalReference)
            .returns(ItemLocationType.STATION, Spaceship::getLocationType)
            .returns(MARKET_ID, Spaceship::getMarketId)
            .returns(NAME, Spaceship::getItemName)
            .returns(STAR_SYSTEM_ID, Spaceship::getStarSystemId);
    }
}