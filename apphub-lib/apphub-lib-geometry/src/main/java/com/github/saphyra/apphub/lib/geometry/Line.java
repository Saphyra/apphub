package com.github.saphyra.apphub.lib.geometry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
//TODO unit test
public class Line {
    private Coordinate a;
    private Coordinate b;

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
