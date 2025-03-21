package com.github.saphyra.apphub.lib.geometry.n_dimension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class NDimensionDistanceCalculatorTest {
    @InjectMocks
    private NDimensionDistanceCalculator underTest;

    @Test
    void nullInput() {
        assertThat(underTest.calculateDistance(null)).isEqualTo(null);
    }

    @Test
    void emptyArrayInput() {
        assertThat(underTest.calculateDistance(new NDimensionCoordinate[0])).isEqualTo(null);
    }

    @Test
    void singleWaypointInput() {
        assertThat(underTest.calculateDistance(new NDimensionCoordinate(1d, 2d, 3d))).isEqualTo(0);
    }

    @Test
    void differentDimensionalCoordinates() {
        assertThat(catchThrowable(() -> underTest.calculateDistance(
            new NDimensionCoordinate(0d, 0d, 0d),
            new NDimensionCoordinate(6d, 8d)
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void calculateDistanceOfRoute() {
        assertThat(underTest.calculateDistance(
            new NDimensionCoordinate(0d, 0d, 0d),
            new NDimensionCoordinate(6d, 8d, 0d),
            new NDimensionCoordinate(12d, 16d, 0d)
        )).isEqualTo(20);
    }
}