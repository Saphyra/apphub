package com.github.saphyra.apphub.lib.geometry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Coordinate {
    private Double x;
    private Double y;

    public Coordinate(Integer x, Integer y) {
        this.x = Double.valueOf(x);
        this.y = Double.valueOf(y);
    }

    public Coordinate(Long x, Long y) {
        this.x = Double.valueOf(x);
        this.y = Double.valueOf(y);
    }

    public Coordinate add(Coordinate coordinate) {
        return new Coordinate(x + coordinate.x, y + coordinate.y);
    }

    public Coordinate minus(Coordinate coordinate) {
        return new Coordinate(x - coordinate.x, y - coordinate.y);
    }
}
