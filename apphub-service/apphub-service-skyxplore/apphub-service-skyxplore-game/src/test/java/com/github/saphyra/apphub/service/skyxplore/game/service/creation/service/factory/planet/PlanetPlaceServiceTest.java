package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.RandomCoordinateProvider;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SolarSystemProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

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
    private GameProperties properties;

    @InjectMocks
    private PlanetPlaceService underTest;

    @Mock
    private SolarSystemProperties solarSystemProperties;

    @Mock
    private Coordinate planetCoordinate;

    @Mock
    private Coordinate existingCoordinate;

    @Before
    public void setUp() {
        given(properties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getMinPlanetDistance()).willReturn(MIN_DISTANCE);
    }

    @Test
    public void tooCloseToStar() {
        given(randomCoordinateProvider.getCoordinateInCircle(SYSTEM_RADIUS)).willReturn(planetCoordinate);
        given(distanceCalculator.getDistance(GameConstants.ORIGO, planetCoordinate)).willReturn(MIN_DISTANCE - 1d);

        Optional<Coordinate> result = underTest.placePlanet(SYSTEM_RADIUS, Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    public void placePlanet() {
        given(randomCoordinateProvider.getCoordinateInCircle(SYSTEM_RADIUS)).willReturn(planetCoordinate);
        given(distanceCalculator.getDistance(GameConstants.ORIGO, planetCoordinate)).willReturn(MIN_DISTANCE + 1d);
        doReturn(MIN_DISTANCE + 1d).when(distanceCalculator).getDistance(existingCoordinate, planetCoordinate);

        Optional<Coordinate> result = underTest.placePlanet(SYSTEM_RADIUS, Arrays.asList(existingCoordinate));

        assertThat(result).contains(planetCoordinate);
    }

    @Test
    public void tooClosePlanet() {
        given(randomCoordinateProvider.getCoordinateInCircle(SYSTEM_RADIUS)).willReturn(planetCoordinate);
        given(distanceCalculator.getDistance(GameConstants.ORIGO, planetCoordinate)).willReturn(MIN_DISTANCE + 1d);
        doReturn(MIN_DISTANCE - 1d).when(distanceCalculator).getDistance(existingCoordinate, planetCoordinate);

        Optional<Coordinate> result = underTest.placePlanet(SYSTEM_RADIUS, Arrays.asList(existingCoordinate));

        assertThat(result).isEmpty();
    }
}