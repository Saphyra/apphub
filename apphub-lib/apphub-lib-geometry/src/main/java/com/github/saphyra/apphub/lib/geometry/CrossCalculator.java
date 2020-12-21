package com.github.saphyra.apphub.lib.geometry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class CrossCalculator {
    private static final double ACCURACY = 1E-10;

    private final DistanceCalculator distanceCalculator;

    public Optional<Coordinate> getCrossPointOfSections(Line l1, Line l2, boolean inclusive) {
        return getCrossPointOfLines(l1, l2)
            .filter(coordinate -> isCoordinateOnSections(l1, l2, coordinate, inclusive));
    }

    private boolean isCoordinateOnSections(Line l1, Line l2, Coordinate coordinate, boolean inclusive) {
        return isCoordinateOnSection(l1, coordinate, inclusive) && isCoordinateOnSection(l2, coordinate, inclusive);
    }

    private boolean isCoordinateOnSection(Line line, Coordinate coordinate, boolean inclusive) {
        double sectionLength = distanceCalculator.getLength(line);

        double distanceFromA = distanceCalculator.getDistance(line.getA(), coordinate);
        double distanceFromB = distanceCalculator.getDistance(line.getB(), coordinate);
        double totalDistance = distanceFromA + distanceFromB;

        double difference = Math.abs(sectionLength - totalDistance);


        if (isEndpoint(distanceFromA, distanceFromB) && !inclusive) {
            return false;
        }

        boolean result = difference < ACCURACY;
        if (result) {
            log.debug("Coordinate {} is on section {} with difference {}", coordinate, line, difference);
        }
        return result;
    }

    private boolean isEndpoint(double distanceFromA, double distanceFromB) {
        return distanceFromA < ACCURACY || distanceFromB < ACCURACY;
    }

    public Optional<Coordinate> getCrossPointOfLines(Line l1, Line l2) {
        double a1 = l1.getB().getY() - l1.getA().getY();
        double b1 = l1.getA().getX() - l1.getB().getX();
        double c1 = a1 * l1.getA().getX() + b1 * l1.getA().getY();

        double a2 = l2.getB().getY() - l2.getA().getY();
        double b2 = l2.getA().getX() - l2.getB().getX();
        double c2 = a2 * l2.getA().getX() + b2 * l2.getA().getY();

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            log.debug("Lines are parallel");
            return Optional.empty();
        }

        double x = (b2 * c1 - b1 * c2) / determinant;
        double y = (a1 * c2 - a2 * c1) / determinant;

        Coordinate coordinate = new Coordinate(x, y);

        return Optional.of(coordinate);
    }
}
