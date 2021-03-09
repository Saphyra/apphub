package com.github.saphyra.apphub.lib.geometry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class Cross {
    private final Line line1;
    private final Line line2;
    private final Coordinate crossPoint;

    public Line getOther(Line line) {
        return line.equals(line1) ? line2 : line1;
    }
}
