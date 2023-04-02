package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.lib.common_util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EmptyPlanetSelectorTest {
    @Mock
    private Random random;

    @InjectMocks
    private EmptyPlanetSelector underTest;

    @Test
    void selectEmptyPlanet() {
        given(random.randInt(0, 1)).willReturn(1);

        int result = underTest.selectEmptyPlanet(new UUID[]{null, UUID.randomUUID(), null});

        assertThat(result).isEqualTo(2);
    }
}