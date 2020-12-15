package com.github.saphyra.apphub.lib.geometry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;

@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class CrossCalculator {
    private final DistanceCalculator distanceCalculator;

    public Optional<Coordinate> getCrossPointOfSections(Line l1, Line l2, boolean inclusive) {
        return getCrossPointOfLines(l1, l2)
            .filter(coordinate -> isCoordinateOnSections(l1, l2, coordinate, inclusive));
    }

    private boolean isCoordinateOnSections(Line l1, Line l2, Coordinate coordinate, boolean inclusive) {
        return isCoordinateOnSection(l1, coordinate, inclusive) && isCoordinateOnSection(l2, coordinate, inclusive);
    }

    private boolean isCoordinateOnSection(Line line, Coordinate coordinate, boolean inclusive) {
        BigDecimal sectionLength = distanceCalculator.getLength(line);

        BigDecimal distanceFromA = distanceCalculator.getDistance(line.getA(), coordinate);
        BigDecimal distanceFromB = distanceCalculator.getDistance(line.getB(), coordinate);
        BigDecimal totalDistance = distanceFromA.add(distanceFromB);

        BigDecimal difference = sectionLength.subtract(totalDistance);


        if (isEndpoint(distanceFromA, distanceFromB) && !inclusive) {
            return false;
        }

        return difference.compareTo(ZERO) == 0;
    }

    private boolean isEndpoint(BigDecimal distanceFromA, BigDecimal distanceFromB) {
        return distanceFromA.compareTo(ZERO) == 0 || distanceFromB.compareTo(ZERO) == 0;
    }

    public Optional<Coordinate> getCrossPointOfLines(Line l1, Line l2) {
        BigDecimal a1 = l1.getB().getY().subtract(l1.getA().getY());
        BigDecimal b1 = l1.getA().getX().subtract(l1.getB().getX());
        BigDecimal c1 = a1.multiply(l1.getA().getX()).add(b1.multiply(l1.getA().getY()));

        BigDecimal a2 = l2.getB().getY().subtract(l2.getA().getY());
        BigDecimal b2 = l2.getA().getX().subtract(l2.getB().getX());
        BigDecimal c2 = a2.multiply(l2.getA().getX()).add(b2.multiply(l2.getA().getY()));

        BigDecimal determinant = a1.multiply(b2).subtract(a2.multiply(b1));

        if (determinant.compareTo(ZERO) == 0) {
            log.debug("Lines are parallel");
            return Optional.empty();
        }

        BigDecimal x = b2.multiply(c1).subtract(b1.multiply(c2)).divide(determinant);
        BigDecimal y = a1.multiply(c2).subtract(a2.multiply(c1)).divide(determinant);

        Coordinate coordinate = new Coordinate(x, y);

        return Optional.of(coordinate);
    }
}
