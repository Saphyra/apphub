package com.github.saphyra.apphub.lib.geometry.n_dimension;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//TODO unit test
public class NDimensionDistanceCalculator {
    public Double calculateDistance(NDimensionCoordinate... coordinates) {
        if (coordinates.length == 0) {
            throw new IllegalArgumentException("Provide at least 1 coordinate");
        }

        double distance = 0d;
        NDimensionCoordinate previousWaypoint = coordinates[0];

        for (int i = 1; i < coordinates.length; i++) {
            NDimensionCoordinate nextWaypoint = coordinates[i];

            distance += calculateDistance(previousWaypoint, nextWaypoint);
            previousWaypoint = nextWaypoint;
        }

        return distance;
    }

    public Double calculateDistance(NDimensionCoordinate coordinate1, NDimensionCoordinate coordinate2) {
        if (coordinate1.numberOfDimensions() != coordinate2.numberOfDimensions()) {
            throw new IllegalArgumentException("Coordinate1 has %s dimensions, while coordinate2 has %s.".formatted(coordinate1.numberOfDimensions(), coordinate2.numberOfDimensions()));
        }

        double sum = 0d;
        for (int i = 0; i < coordinate1.numberOfDimensions(); i++) {
            Double c1 = coordinate1.getPoints().get(i);
            Double c2 = coordinate2.getPoints().get(i);
            sum += Math.pow(c1 - c2, 2);
        }

        return Math.sqrt(sum);
    }
}
