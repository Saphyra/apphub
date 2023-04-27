package com.github.saphyra.apphub.lib.geometry;


import com.github.saphyra.apphub.lib.common_util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RandomCoordinateProviderTest {
    private static final double RADIUS = 100;
    private static final Coordinate ORIGO = new Coordinate(0, 0);

    @Spy
    private final Random random = new Random();

    @InjectMocks
    private RandomCoordinateProvider underTest;

    private final DistanceCalculator distanceCalculator = new DistanceCalculator();

    @Test
    void getCoordinateOnCircle() {
        for (int i = 0; i < 100; i++) {
            Coordinate result = underTest.getCoordinateOnCircle(RADIUS);

            assertThat(distanceCalculator.getDistance(ORIGO, result)).isBetween(RADIUS - 1, RADIUS + 1);
        }
    }
}