package com.github.saphyra.apphub.lib.geometry;

import lombok.extern.slf4j.Slf4j;

//TODO unit test
@Slf4j
public class DistanceCalculator {
    public double getDistance(Coordinate c1, Coordinate c2) {
        double d1 = c1.getX() - c2.getX();
        double d2 = c1.getY() - c2.getY();

        double p1 = d1 * d1;
        double p2 = d2 * d2;
        double result = Math.sqrt(p1 + p2);
        log.debug("Distance between coordinates {}, {}: {}", c1, c2, result);
        return result;
    }

    public double getLength(Line line) {
        return getDistance(line.getA(), line.getB());
    }
}
