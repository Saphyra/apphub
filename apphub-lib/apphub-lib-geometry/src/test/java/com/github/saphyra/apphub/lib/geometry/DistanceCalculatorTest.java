package com.github.saphyra.apphub.lib.geometry;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DistanceCalculatorTest {
    @InjectMocks
    private DistanceCalculator underTest;

    @Test
    public void getDistance_equalCoordinates() {
        Coordinate coordinate = new Coordinate(0, 0);

        double result = underTest.getDistance(coordinate, coordinate);

        assertThat(result).isEqualTo(0);
    }

    @Test
    public void getLength() {
        Line line = new Line(
            new Coordinate(0, 0),
            new Coordinate(5, 0)
        );

        double result = underTest.getLength(line);

        assertThat(result).isEqualTo(5);
    }

    @Test
    public void getDistanceFromLine() {
        Line line = new Line(
            new Coordinate(0, 0),
            new Coordinate(0, 10)
        );
        Coordinate coordinate = new Coordinate(5, 5);

        double result = underTest.getDistance(coordinate, line);

        assertThat(Math.round(result)).isEqualTo(5);
    }

    @Test
    public void getDistanceOfRoute() {
        Coordinate coordinate1 = new Coordinate(0, 0);
        Coordinate coordinate2 = new Coordinate(0, 5);
        Coordinate coordinate3 = new Coordinate(0, 11);

        double result = underTest.getDistance(Arrays.asList(coordinate1, coordinate2, coordinate3));

        assertThat(result).isEqualTo(11);
    }
}