package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
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

    @InjectMocks
    private SpaceshipFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME))
            .returns(EXTERNAL_REFERENCE, Spaceship::getExternalReference)
            .returns(ItemLocationType.STATION, Spaceship::getLocationType)
            .returns(MARKET_ID, Spaceship::getMarketId)
            .returns(NAME, Spaceship::getItemName);
    }
}