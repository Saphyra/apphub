package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RandomEmptyPlanetFinderTest {
    @Mock
    private Random random;

    @InjectMocks
    private RandomEmptyPlanetFinder underTest;

    @Mock
    private Universe universe;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Planet planet1;

    @Mock
    private Planet planet2;

    @Test
    public void randomEmptyPlanet_fromUniverse() {
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.toMap(new BiWrapper<>(UUID.randomUUID(), planet1), new BiWrapper<>(UUID.randomUUID(), planet2)));
        given(planet1.getOwner()).willReturn(UUID.randomUUID());
        given(random.randInt(0, 0)).willReturn(0);

        Planet result = underTest.randomEmptyPlanet(Arrays.asList(solarSystem));

        assertThat(result).isEqualTo(planet2);
    }

    @Test
    public void randomEmptyPlanet_fromSystem() {
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.toMap(new BiWrapper<>(UUID.randomUUID(), planet1), new BiWrapper<>(UUID.randomUUID(), planet2)));
        given(planet1.getOwner()).willReturn(UUID.randomUUID());
        given(random.randInt(0, 0)).willReturn(0);

        Planet result = underTest.randomEmptyPlanet(solarSystem);

        assertThat(result).isEqualTo(planet2);
    }
}