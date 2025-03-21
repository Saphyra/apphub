package com.github.saphyra.apphub.lib.geometry.n_dimension;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class NDimensionCoordinate {
    private final List<Double> points;

    public NDimensionCoordinate(Double... coordinates){
        this.points = Arrays.asList(coordinates);
    }

    public int numberOfDimensions() {
        return points.size();
    }
}
