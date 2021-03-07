package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.home_planet.closest_system;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class NextWaypointSelectorTest {
    @Mock
    private ClosestSystemFinder closestSystemFinder;

    @InjectMocks
    private NextWaypointSelector underTest;

    @Mock
    private SolarSystem referenceSystem;

    @Mock
    private Universe universe;

    @Mock
    private Coordinate inRouteCoordinate;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Coordinate coordinate3;

    @Mock
    private Line line1;

    @Mock
    private Line line2;

    @Mock
    private Line line3;

    @Mock
    private SolarSystem solarSystem1;

    @Mock
    private SolarSystem solarSystem2;

    @Mock
    private SolarSystem solarSystem3;

    @Test
    public void findNextWaypoint() {
        given(referenceSystem.getCoordinate()).willReturn(inRouteCoordinate);

        given(line1.getOtherEndpoint(inRouteCoordinate)).willReturn(coordinate1);
        given(line2.getOtherEndpoint(inRouteCoordinate)).willReturn(coordinate2);
        given(line3.getOtherEndpoint(inRouteCoordinate)).willReturn(coordinate3);

        Map<Coordinate, SolarSystem> systems = new HashMap<Coordinate, SolarSystem>() {{
            put(coordinate1, solarSystem1);
            put(coordinate2, solarSystem2);
            put(coordinate3, solarSystem3);
        }};
        given(universe.getSystems()).willReturn(systems);

        given(closestSystemFinder.getClosestSystemWithEmptyPlanet(solarSystem1, universe, Arrays.asList(inRouteCoordinate, coordinate1))).willReturn(new BiWrapper<>(solarSystem1, 5d));
        given(closestSystemFinder.getClosestSystemWithEmptyPlanet(solarSystem2, universe, Arrays.asList(inRouteCoordinate, coordinate2))).willReturn(new BiWrapper<>(solarSystem2, 3d));
        given(closestSystemFinder.getClosestSystemWithEmptyPlanet(solarSystem3, universe, Arrays.asList(inRouteCoordinate, coordinate3))).willReturn(new BiWrapper<>(solarSystem3, 5d));

        BiWrapper<SolarSystem, Double> result = underTest.findNextWaypoint(referenceSystem, universe, Arrays.asList(inRouteCoordinate), Arrays.asList(line1, line2, line3), closestSystemFinder);

        assertThat(result.getEntity1()).isEqualTo(solarSystem2);
        assertThat(result.getEntity2()).isEqualTo(3d);
    }
}