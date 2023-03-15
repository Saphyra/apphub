package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.home_planet;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FreePlanetCounterTest {
    @InjectMocks
    private FreePlanetCounter underTest;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Planet planet1;

    @Mock
    private Planet planet2;

    @Test
    public void getNumberOfFreePlanets() {
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.toMap(new BiWrapper<>(UUID.randomUUID(), planet1), new BiWrapper<>(UUID.randomUUID(), planet2)));
        given(planet1.getOwner()).willReturn(UUID.randomUUID());

        long result = underTest.getNumberOfFreePlanets(solarSystem);

        assertThat(result).isEqualTo(1);
    }
}