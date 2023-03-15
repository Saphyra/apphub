package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class PlanetListPlaceServiceTest {
    private static final int EXPECTED_AMOUNT = 3;
    private static final int RADIUS = 435;

    @Mock
    private PlanetPlaceService planetPlaceService;

    @InjectMocks
    private PlanetListPlaceService underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Test
    public void placePlanets() {
        given(planetPlaceService.placePlanet(RADIUS, Collections.emptyList())).willReturn(Optional.of(coordinate1));
        given(planetPlaceService.placePlanet(RADIUS, Arrays.asList(coordinate1))).willReturn(Optional.empty())
            .willReturn(Optional.of(coordinate2));

        List<Coordinate> result = underTest.placePlanets(EXPECTED_AMOUNT, RADIUS);

        assertThat(result).containsExactly(coordinate1, coordinate2);
    }
}