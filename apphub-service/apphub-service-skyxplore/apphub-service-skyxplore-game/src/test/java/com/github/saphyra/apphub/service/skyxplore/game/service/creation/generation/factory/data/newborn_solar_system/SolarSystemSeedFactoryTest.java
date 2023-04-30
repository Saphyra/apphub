package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_domain.Range;
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
class SolarSystemSeedFactoryTest {
    private static final Range<Integer> PLANETS_PER_SOLAR_SYSTEM = new Range<>(0, 0);
    @Mock
    private Random random;

    @InjectMocks
    private SolarSystemSeedFactory underTest;

    @Mock
    private SkyXploreGameSettings settings;

    @Test
    void newSolarSystem() {
        given(settings.getPlanetsPerSolarSystem()).willReturn(PLANETS_PER_SOLAR_SYSTEM);
        given(random.randInt(1, 1)).willReturn(2);

        UUID[] result = underTest.newSolarSystem(settings, true);

        assertThat(result).hasSize(2);
    }
}