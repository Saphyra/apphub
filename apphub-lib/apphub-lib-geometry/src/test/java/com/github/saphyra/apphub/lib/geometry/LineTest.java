package com.github.saphyra.apphub.lib.geometry;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
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

}