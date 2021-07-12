package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.closest_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ClosestHabitableSystemFinderTest {
    @Mock
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private ClosestHabitableSystemFinder underTest;

    @Mock
    private SolarSystem referenceSystem;

    @Mock
    private Line line1;

    @Mock
    private Line line2;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate systemCoordinate;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private SolarSystem solarSystem1;

    @Mock
    private SolarSystem solarSystem2;

    @Mock
    private Planet planet1;

    @Mock
    private Planet planet2;

    @Before
    public void setUp() {
        given(coordinateModel.getCoordinate()).willReturn(systemCoordinate);
    }

    @Test
    public void findClosestHabitableSystem_found() {
        given(line1.getLength(distanceCalculator)).willReturn(10d);
        given(line2.getLength(distanceCalculator)).willReturn(5d);

        given(referenceSystem.getCoordinate()).willReturn(coordinateModel);
        given(line2.getOtherEndpoint(systemCoordinate)).willReturn(coordinate2);

        Map<Coordinate, SolarSystem> systems = new HashMap<Coordinate, SolarSystem>() {{
            put(coordinate1, solarSystem1);
            put(coordinate2, solarSystem2);
        }};

        given(solarSystem2.getPlanets()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), planet2));

        Optional<SolarSystem> result = underTest.findClosestHabitableSystem(referenceSystem, systems, Arrays.asList(line1, line2));

        assertThat(result).contains(solarSystem2);
    }

    @Test
    public void findClosestHabitableSystem_NotFound() {
        given(line1.getLength(distanceCalculator)).willReturn(10d);
        given(line2.getLength(distanceCalculator)).willReturn(5d);

        given(referenceSystem.getCoordinate()).willReturn(coordinateModel);
        given(line1.getOtherEndpoint(systemCoordinate)).willReturn(coordinate1);
        given(line2.getOtherEndpoint(systemCoordinate)).willReturn(coordinate2);

        Map<Coordinate, SolarSystem> systems = new HashMap<Coordinate, SolarSystem>() {{
            put(coordinate1, solarSystem1);
            put(coordinate2, solarSystem2);
        }};

        given(solarSystem1.getPlanets()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), planet1));
        given(solarSystem2.getPlanets()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), planet2));

        given(planet1.getOwner()).willReturn(UUID.randomUUID());
        given(planet2.getOwner()).willReturn(UUID.randomUUID());

        Optional<SolarSystem> result = underTest.findClosestHabitableSystem(referenceSystem, systems, Arrays.asList(line1, line2));

        assertThat(result).isEmpty();
    }
}