package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@RunWith(MockitoJUnitRunner.class)
public class AllianceMemberSystemFinderTest {
    private static final UUID USER_ID = UUID.randomUUID();

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
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private SolarSystem solarSystem1;

    @Mock
    private SolarSystem solarSystem2;

    @Mock
    private Planet planet;

    Map<Coordinate, SolarSystem> solarSystemMap;

    @Before
    public void setUp() {
        solarSystemMap = CollectionUtils.toMap(
            new BiWrapper<>(coordinate1, solarSystem1),
            new BiWrapper<>(coordinate2, solarSystem2)
        );
    }

    @Test
    public void fullAllianceSystem() {
        given(homeSystemFinder.findHomeSystem(Arrays.asList(USER_ID), solarSystemMap.values())).willReturn(Optional.of(solarSystem1));
        given(freePlanetCounter.getNumberOfFreePlanets(solarSystem1)).willReturn(0L);
        given(closestSystemFinder.getClosestSystemWithEmptyPlanet(solarSystem1, solarSystemMap)).willReturn(solarSystem2);
        given(randomEmptyPlanetFinder.randomEmptyPlanet(solarSystem2)).willReturn(planet);

        Planet result = underTest.findAllianceMemberSystem(Arrays.asList(USER_ID), solarSystemMap);

        assertThat(result).isEqualTo(planet);
    }

    @Test
    public void allianceSystem() {
        given(homeSystemFinder.findHomeSystem(Arrays.asList(USER_ID), solarSystemMap.values())).willReturn(Optional.of(solarSystem1));
        given(freePlanetCounter.getNumberOfFreePlanets(solarSystem1)).willReturn(1L);
        given(randomEmptyPlanetFinder.randomEmptyPlanet(solarSystem1)).willReturn(planet);

        Planet result = underTest.findAllianceMemberSystem(Arrays.asList(USER_ID), solarSystemMap);

        assertThat(result).isEqualTo(planet);
    }

    @Test
    public void noHomeSystem() {
        given(homeSystemFinder.findHomeSystem(Arrays.asList(USER_ID), solarSystemMap.values())).willReturn(Optional.empty());
        given(randomEmptyPlanetFinder.randomEmptyPlanet(solarSystemMap.values())).willReturn(planet);

        Planet result = underTest.findAllianceMemberSystem(Arrays.asList(USER_ID), solarSystemMap);

        assertThat(result).isEqualTo(planet);
    }
}