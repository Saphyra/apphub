package com.github.saphyra.apphub.lib.geometry;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

//TODO unit test
@Slf4j
public class DistanceCalculator {
    public BigDecimal getDistance(Coordinate c1, Coordinate c2) {
        BigDecimal d1 = c1.getX().subtract(c2.getX());
        BigDecimal d2 = c1.getY().subtract(c2.getY());

        BigDecimal p1 = d1.multiply(d1);
        BigDecimal p2 = d2.multiply(d2);
        BigDecimal sum = p1.add(p2);
        BigDecimal result = BigDecimalUtils.sqrt(sum);
        log.debug("Distance between coordinates {}, {}: {}", c1, c2, result);
        return result;
    }

    public BigDecimal getLength(Line line) {
        return getDistance(line.getA(), line.getB());
    }
}
