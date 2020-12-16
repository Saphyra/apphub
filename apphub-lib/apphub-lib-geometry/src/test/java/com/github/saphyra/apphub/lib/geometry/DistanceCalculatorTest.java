package com.github.saphyra.apphub.lib.geometry;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DistanceCalculatorTest {
    @InjectMocks
    private DistanceCalculator underTest;

    @Test
    public void equalCoordinates() {
        Coordinate coordinate = new Coordinate(0, 0);

        double result = underTest.getDistance(coordinate, coordinate);

        assertThat(result).isEqualTo(0);
    }

}