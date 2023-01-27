package com.github.saphyra.apphub.lib.geometry;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CrossCalculatorTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private CrossCalculator underTest;

    @Test
    public void getCrossPointsOfSections_hasCrossPoint() {
        Line line1 = new Line(
            new Coordinate(-1, -1),
            new Coordinate(1, 1)
        );
        Line line2 = new Line(
            new Coordinate(-1, 1),
            new Coordinate(1, -1)
        );

        Optional<Cross> result = underTest.getCrossPointOfSections(line1, line2, false);

        assertThat(result).isNotEmpty();
        assertThat(result.get().getCrossPoint()).isEqualTo(new Coordinate(0, 0));
    }

    @Test
    public void getCrossPointsOfSections_exclusive() {
        Line line1 = new Line(
            new Coordinate(-1, -1),
            new Coordinate(1, 1)
        );
        Line line2 = new Line(
            new Coordinate(-1, -1),
            new Coordinate(2, -1)
        );

        Optional<Cross> result = underTest.getCrossPointOfSections(line1, line2, false);

        assertThat(result).isEmpty();
    }

    @Test
    public void getCrossPointsOfSections_inclusive() {
        Line line1 = new Line(
            new Coordinate(-1, -1),
            new Coordinate(1, 1)
        );
        Line line2 = new Line(
            new Coordinate(-1, -1),
            new Coordinate(2, -1)
        );

        Optional<Cross> result = underTest.getCrossPointOfSections(line1, line2, true);

        assertThat(result).isNotEmpty();
        assertThat(result.get().getCrossPoint()).isEqualTo(new Coordinate(-1, -1));
    }

    @Test
    public void getCrossPointOfLines() {
        Line line1 = new Line(
            new Coordinate(-1, -1),
            new Coordinate(1, 1)
        );
        Line line2 = new Line(
            new Coordinate(-1, 1),
            new Coordinate(1, -1)
        );

        Optional<Coordinate> result = underTest.getCrossPointOfLines(line1, line2);

        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(new Coordinate(0, 0));
    }

    @Test
    public void getCrossPointOfLines_parallel_lines() {
        Line line1 = new Line(
            new Coordinate(1, 1),
            new Coordinate(2, 1)
        );
        Line line2 = new Line(
            new Coordinate(1, 2),
            new Coordinate(2, 2)
        );

        Optional<Coordinate> result = underTest.getCrossPointOfLines(line1, line2);

        assertThat(result).isEmpty();
    }
}