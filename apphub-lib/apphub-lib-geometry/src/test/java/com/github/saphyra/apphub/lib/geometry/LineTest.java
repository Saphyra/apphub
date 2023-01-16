package com.github.saphyra.apphub.lib.geometry;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LineTest {
    private static final double DISTANCE = 34;

    @Mock
    private DistanceCalculator distanceCalculator;

    @Test
    public void getLength() {
        Coordinate c1 = new Coordinate(0, 1);
        Coordinate c2 = new Coordinate(2, 3);

        Line underTest = new Line(c1, c2);

        given(distanceCalculator.getLength(underTest)).willReturn(DISTANCE);

        double result1 = underTest.getLength(distanceCalculator);
        double result2 = underTest.getLength(distanceCalculator);

        assertThat(result1)
            .isEqualTo(result2)
            .isEqualTo(DISTANCE);

        verify(distanceCalculator, times(1)).getLength(underTest);
    }

    @Test
    public void equivalence() {
        Coordinate c1 = new Coordinate(0, 1);
        Coordinate c2 = new Coordinate(2, 3);

        Line line1 = new Line(c1, c2);
        Line line2 = new Line(c2, c1);

        assertThat(line1).isEqualTo(line2);
        assertThat(line1.hashCode()).isEqualTo(line2.hashCode());
    }

    @Test
    public void notEqual() {
        Coordinate c1 = new Coordinate(0, 1);
        Coordinate c2 = new Coordinate(2, 3);
        Coordinate c3 = new Coordinate(4, 5);

        Line line1 = new Line(c1, c2);
        Line line2 = new Line(c2, c3);

        assertThat(line1).isNotEqualTo(line2);
        assertThat(line1.hashCode()).isNotEqualTo(line2.hashCode());
    }

    @Test
    public void isEndpoint() {
        Coordinate c1 = new Coordinate(0, 1);
        Coordinate c2 = new Coordinate(2, 3);
        Coordinate c3 = new Coordinate(5, 3);

        Line underTest = new Line(c1, c2);

        assertThat(underTest.isEndpoint(c1)).isTrue();
        assertThat(underTest.isEndpoint(c2)).isTrue();
        assertThat(underTest.isEndpoint(c3)).isFalse();
    }

    @Test
    public void getOtherEndpoint() {
        Coordinate c1 = new Coordinate(0, 1);
        Coordinate c2 = new Coordinate(2, 3);

        Line underTest = new Line(c1, c2);

        assertThat(underTest.getOtherEndpoint(c1)).isEqualTo(c2);
        assertThat(underTest.getOtherEndpoint(c2)).isEqualTo(c1);
    }

    @Test
    public void getOtherEndpoint_notEndpoint() {
        Coordinate c1 = new Coordinate(0, 1);
        Coordinate c2 = new Coordinate(2, 3);
        Coordinate c3 = new Coordinate(5, 3);

        Line underTest = new Line(c1, c2);

        Throwable ex = catchThrowable(() -> underTest.getOtherEndpoint(c3));

        assertThat(ex).isInstanceOf(IllegalArgumentException.class);
    }
}