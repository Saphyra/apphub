package com.github.saphyra.apphub.lib.geometry;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@NoArgsConstructor
@Data
//TODO unit test
public class Line {
    private Coordinate a;
    private Coordinate b;
    private Double length = null;

    @Builder
    public Line(Coordinate a, Coordinate b) {
        this.a = a;
        this.b = b;
    }

    public double getLength(DistanceCalculator distanceCalculator) {
        if (isNull(length)) {
            length = distanceCalculator.getLength(this);
        }
        return length;
    }

    @Override
    public int hashCode() {
        return a.hashCode() * b.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Line) {
            Line l = (Line) o;

            return (a.equals(l.a) && b.equals(l.b))
                || (a.equals(l.b) && b.equals(l.a));
        }
        return false;
    }

    public boolean isEndpoint(Coordinate system) {
        return a.equals(system) || b.equals(system);
    }
}
