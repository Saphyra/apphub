package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.closest_system;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClosestSystemFinderTest {
    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private WaypointCandidateFilter waypointCandidateFilter;

    @Mock
    private ClosestHabitableSystemFinder closestHabitableSystemFinder;

    @Mock
    private NextWaypointSelector nextWaypointSelector;

    @InjectMocks
    private ClosestSystemFinder underTest;

    @Mock
    private SolarSystem referenceSystem;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Universe universe;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Line line;

    @Test
    public void getClosestSystemWithEmptyPlanet_found() {
        given(referenceSystem.getCoordinate()).willReturn(coordinate1);
        given(waypointCandidateFilter.getWayPointCandidates(referenceSystem, universe, Arrays.asList(coordinate1))).willReturn(Arrays.asList(line));
        Map<Coordinate, SolarSystem> systems = CollectionUtils.singleValueMap(coordinate1, referenceSystem);
        given(universe.getSystems()).willReturn(systems);
        given(closestHabitableSystemFinder.findClosestHabitableSystem(referenceSystem, systems, Arrays.asList(line))).willReturn(Optional.of(solarSystem));
        given(solarSystem.getCoordinate()).willReturn(coordinate2);

        SolarSystem result = underTest.getClosestSystemWithEmptyPlanet(referenceSystem, universe);

        assertThat(result).isEqualTo(solarSystem);
        verify(distanceCalculator).getDistance(Arrays.asList(coordinate1, coordinate2));
    }

    @Test
    public void getClosestSystemWithEmptyPlanet_recursiveStep() {
        given(referenceSystem.getCoordinate()).willReturn(coordinate1);
        given(waypointCandidateFilter.getWayPointCandidates(referenceSystem, universe, Arrays.asList(coordinate1))).willReturn(Arrays.asList(line));
        Map<Coordinate, SolarSystem> systems = CollectionUtils.singleValueMap(coordinate1, referenceSystem);
        given(universe.getSystems()).willReturn(systems);
        given(closestHabitableSystemFinder.findClosestHabitableSystem(referenceSystem, systems, Arrays.asList(line))).willReturn(Optional.empty());
        given(nextWaypointSelector.findNextWaypoint(referenceSystem, universe, Arrays.asList(coordinate1), Arrays.asList(line), underTest)).willReturn(new BiWrapper<>(solarSystem, 213d));

        SolarSystem result = underTest.getClosestSystemWithEmptyPlanet(referenceSystem, universe);

        assertThat(result).isEqualTo(solarSystem);
    }
}