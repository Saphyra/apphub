package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.RandomCoordinateProvider;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemCoordinateListProviderTest {
    private static final int UNIVERSE_SIZE = 324;
    private static final int MIN_DISTANCE = 134;

    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private GameCreationProperties properties;

    @Mock
    private RandomCoordinateProvider randomCoordinateProvider;

    @InjectMocks
    private SolarSystemCoordinateListProvider underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Coordinate coordinate3;

    @Mock
    private GameCreationProperties.SolarSystemProperties solarSystemProperties;

    @Test
    public void getCoordinates() {
        given(properties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getMinSolarSystemDistance()).willReturn(MIN_DISTANCE);

        given(randomCoordinateProvider.getCoordinateInSquare(UNIVERSE_SIZE)).willReturn(coordinate1)
            .willReturn(coordinate2)
            .willReturn(coordinate3);

        given(distanceCalculator.getDistance(coordinate1, coordinate2)).willReturn(MIN_DISTANCE - 1d);
        given(distanceCalculator.getDistance(coordinate1, coordinate3)).willReturn(MIN_DISTANCE + 1d);

        List<Coordinate> result = underTest.getCoordinates(UNIVERSE_SIZE, 3);

        assertThat(result).containsExactly(coordinate1, coordinate3);
    }
}