package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
class PlanetCoordinateProvider {
    private static final Coordinate STAR_COORDINATE = new Coordinate(0, 0);

    private final DistanceCalculator distanceCalculator;
    private final GameCreationProperties properties;
    private final Random random;

    List<Coordinate> getCoordinates(int expectedPlanetAmount, int systemRadius) {
        int minDistance = properties.getSolarSystem()
            .getMinPlanetDistance();

        for (int generationTryCount = 0; generationTryCount < 10; generationTryCount++) {
            log.debug("Generating {} number of planets to a system sized {}. TryCount: {}", expectedPlanetAmount, systemRadius, generationTryCount);
            List<Coordinate> coordinates = new ArrayList<>();

            for (int planetNumber = 0; planetNumber < expectedPlanetAmount; planetNumber++) {
                log.debug("Generating planet #{}...", planetNumber);

                for (int placementTryCount = 0; placementTryCount < 100; placementTryCount++) {
                    log.debug("Trying to place a planet... TryCount: {}", placementTryCount);
                    Coordinate coordinate = getCoordinate(systemRadius);

                    double distanceFromStar = distanceCalculator.getDistance(STAR_COORDINATE, coordinate);
                    log.debug("Distance from star: {}", distanceFromStar);
                    if (distanceFromStar < minDistance) {
                        log.info("Coordinate {} is too close to the star.", distanceFromStar);
                        continue;
                    }

                    boolean noClosePlanets = coordinates.stream()
                        .allMatch(c -> distanceCalculator.getDistance(c, coordinate) > minDistance);

                    if (noClosePlanets) {
                        log.debug("Planet placed to {}", coordinate);
                        coordinates.add(coordinate);
                        break;
                    } else {
                        log.debug("Placement failed, there is an another planet nearby.");
                    }
                }
            }

            if (coordinates.size() == expectedPlanetAmount) {
                return coordinates.stream()
                    .sorted(Comparator.comparingDouble(o -> distanceCalculator.getDistance(o, STAR_COORDINATE)))
                    .collect(Collectors.toList());
            } else {
                log.info("Failed to place all the necessary planets. {} planets were placed, but {} are expected.", coordinates.size(), expectedPlanetAmount);
            }
        }

        throw new RuntimeException("Could not place " + expectedPlanetAmount + " number of planets to a system sized " + systemRadius);
    }

    Coordinate getCoordinate(double systemRadius) {
        double randomDistance = random.randDouble() * 2 * Math.PI;
        double r = systemRadius * Math.sqrt(random.randDouble());

        return new Coordinate(
            Math.floor(Math.cos(randomDistance) * r),
            Math.floor(Math.sin(randomDistance) * r)
        );
    }
}
