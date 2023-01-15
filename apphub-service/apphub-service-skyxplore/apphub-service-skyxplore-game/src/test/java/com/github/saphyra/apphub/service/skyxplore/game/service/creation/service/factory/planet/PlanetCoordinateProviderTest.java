package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PlanetCoordinateProviderTest {
    private static final int EXPECTED_PLANET_AMOUNT = 2;
    private static final int SYSTEM_RADIUS = 45231;

    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private PlanetListPlaceService planetListPlaceService;

    @InjectMocks
    private PlanetCoordinateProvider underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Test
    public void getCoordinates() {
        given(planetListPlaceService.placePlanets(EXPECTED_PLANET_AMOUNT, SYSTEM_RADIUS)).willReturn(Collections.emptyList())
            .willReturn(Arrays.asList(coordinate1, coordinate2));

        given(distanceCalculator.getDistance(coordinate1, GameConstants.ORIGO)).willReturn(10d);
        given(distanceCalculator.getDistance(coordinate2, GameConstants.ORIGO)).willReturn(3d);

        List<Coordinate> result = underTest.getCoordinates(EXPECTED_PLANET_AMOUNT, SYSTEM_RADIUS);

        assertThat(result).containsExactly(coordinate2, coordinate1);
    }

    @Test
    public void placementFailed() {
        given(planetListPlaceService.placePlanets(EXPECTED_PLANET_AMOUNT, SYSTEM_RADIUS)).willReturn(Collections.emptyList());

        assertThat(catchThrowable(() -> underTest.getCoordinates(EXPECTED_PLANET_AMOUNT, SYSTEM_RADIUS))).isInstanceOf(RuntimeException.class);
    }
}