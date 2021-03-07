package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.home_planet.closest_system.ClosestSystemFinder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@RunWith(MockitoJUnitRunner.class)
public class AllianceMemberSystemFinderTest {
    private static final UUID PLAYER = UUID.randomUUID();

    @Mock
    private HomeSystemFinder homeSystemFinder;

    @Mock
    private RandomEmptyPlanetFinder randomEmptyPlanetFinder;

    @Mock
    private FreePlanetCounter freePlanetCounter;

    @Mock
    private ClosestSystemFinder closestSystemFinder;

    @InjectMocks
    private AllianceMemberSystemFinder underTest;

    @Mock
    private SolarSystem solarSystem1;

    @Mock
    private SolarSystem solarSystem2;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Test
    public void fullAllianceSystem() {
        given(homeSystemFinder.findHomeSystem(Arrays.asList(PLAYER), universe)).willReturn(Optional.of(solarSystem1));
        given(freePlanetCounter.getNumberOfFreePlanets(solarSystem1)).willReturn(0l);
        given(closestSystemFinder.getClosestSystemWithEmptyPlanet(solarSystem1, universe)).willReturn(solarSystem2);
        given(randomEmptyPlanetFinder.randomEmptyPlanet(solarSystem2)).willReturn(planet);

        Planet result = underTest.findAllianceMemberSystem(Arrays.asList(PLAYER), universe);

        assertThat(result).isEqualTo(planet);

    }

    @Test
    public void allianceSystem() {
        given(homeSystemFinder.findHomeSystem(Arrays.asList(PLAYER), universe)).willReturn(Optional.of(solarSystem1));
        given(freePlanetCounter.getNumberOfFreePlanets(solarSystem1)).willReturn(1l);
        given(randomEmptyPlanetFinder.randomEmptyPlanet(solarSystem1)).willReturn(planet);

        Planet result = underTest.findAllianceMemberSystem(Arrays.asList(PLAYER), universe);

        assertThat(result).isEqualTo(planet);
    }

    @Test
    public void noHomeSystem() {
        given(homeSystemFinder.findHomeSystem(Arrays.asList(PLAYER), universe)).willReturn(Optional.empty());
        given(randomEmptyPlanetFinder.randomEmptyPlanet(universe)).willReturn(planet);

        Planet result = underTest.findAllianceMemberSystem(Arrays.asList(PLAYER), universe);

        assertThat(result).isEqualTo(planet);
    }
}