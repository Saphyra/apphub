package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.RandomCoordinateProvider;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants.STAR_COORDINATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class PlanetPlaceServiceTest {
    private static final int SYSTEM_RADIUS = 234;
    private static final int MIN_DISTANCE = 2355;

    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private RandomCoordinateProvider randomCoordinateProvider;

    @Mock
    private GameCreationProperties properties;

    @InjectMocks
    private PlanetPlaceService underTest;

    @Mock
    private GameCreationProperties.SolarSystemProperties solarSystemProperties;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Before
    public void setUp() {
        given(properties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getMinPlanetDistance()).willReturn(MIN_DISTANCE);
    }

    @Test
    public void placePlanet_tooCloseToStar() {
        given(randomCoordinateProvider.getCoordinateInCircle(SYSTEM_RADIUS)).willReturn(coordinate1);
        given(distanceCalculator.getDistance(STAR_COORDINATE, coordinate1)).willReturn(MIN_DISTANCE - 1d);

        Optional<Coordinate> result = underTest.placePlanet(SYSTEM_RADIUS, Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    public void placePlanet_nearbyPlanet() {
        given(randomCoordinateProvider.getCoordinateInCircle(SYSTEM_RADIUS)).willReturn(coordinate1);
        given(distanceCalculator.getDistance(STAR_COORDINATE, coordinate1)).willReturn(MIN_DISTANCE + 1d);
        doReturn(MIN_DISTANCE - 1d).when(distanceCalculator).getDistance(coordinate2, coordinate1);

        Optional<Coordinate> result = underTest.placePlanet(SYSTEM_RADIUS, Arrays.asList(coordinate2));

        assertThat(result).isEmpty();
    }

    @Test
    public void placePlanet_placed() {
        given(randomCoordinateProvider.getCoordinateInCircle(SYSTEM_RADIUS)).willReturn(coordinate1);
        given(distanceCalculator.getDistance(STAR_COORDINATE, coordinate1)).willReturn(MIN_DISTANCE + 1d);
        doReturn(MIN_DISTANCE + 1d).when(distanceCalculator).getDistance(coordinate2, coordinate1);

        Optional<Coordinate> result = underTest.placePlanet(SYSTEM_RADIUS, Arrays.asList(coordinate2));

        assertThat(result).contains(coordinate1);
    }
}