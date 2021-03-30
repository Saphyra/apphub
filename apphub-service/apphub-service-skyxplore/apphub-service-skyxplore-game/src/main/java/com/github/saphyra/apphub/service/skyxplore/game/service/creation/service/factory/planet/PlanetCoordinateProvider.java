package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants.STAR_COORDINATE;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetCoordinateProvider {
    private final DistanceCalculator distanceCalculator;
    private final PlanetListPlaceService planetListPlaceService;

    List<Coordinate> getCoordinates(int expectedPlanetAmount, int systemRadius) {
        for (int generationTryCount = 0; generationTryCount < 10; generationTryCount++) {
            log.debug("Generating {} number of planets to a system sized {}. TryCount: {}", expectedPlanetAmount, systemRadius, generationTryCount);
            List<Coordinate> coordinates = planetListPlaceService.placePlanets(expectedPlanetAmount, systemRadius);

            if (coordinates.size() == expectedPlanetAmount) {
                return coordinates.stream()
                    .sorted(Comparator.comparingDouble(o -> distanceCalculator.getDistance(o, STAR_COORDINATE))) //Sorting is needed for name generation - Closest Planet gets "A" suffix
                    .collect(Collectors.toList());
            } else {
                log.info("Failed to place all the necessary planets. {} planets were placed, but {} are expected.", coordinates.size(), expectedPlanetAmount);
            }
        }

        throw new RuntimeException("Could not place " + expectedPlanetAmount + " number of planets to a system sized " + systemRadius);
    }
}
