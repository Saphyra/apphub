package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.RandomCoordinateProvider;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants.STAR_COORDINATE;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetPlaceService {
    private final DistanceCalculator distanceCalculator;
    private final RandomCoordinateProvider randomCoordinateProvider;
    private final GameCreationProperties properties;

    Optional<Coordinate> placePlanet(int systemRadius, List<Coordinate> coordinates) {
        int minDistance = properties.getSolarSystem()
            .getMinPlanetDistance();

        for (int placementTryCount = 0; placementTryCount < 100; placementTryCount++) {
            log.debug("Trying to place a planet... TryCount: {}", placementTryCount);
            Coordinate coordinate = randomCoordinateProvider.getCoordinateInCircle(systemRadius);

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
                return Optional.of(coordinate);
            } else {
                log.debug("Placement failed, there is an another planet nearby.");
            }
        }

        return Optional.empty();
    }
}
