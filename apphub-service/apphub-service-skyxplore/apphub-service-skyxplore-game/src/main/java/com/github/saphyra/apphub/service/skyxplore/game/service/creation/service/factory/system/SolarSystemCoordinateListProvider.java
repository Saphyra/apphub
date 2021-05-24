package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.RandomCoordinateProvider;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemCoordinateListProvider {
    private final DistanceCalculator distanceCalculator;
    private final GameCreationProperties properties;
    private final RandomCoordinateProvider randomCoordinateProvider;

    List<Coordinate> getCoordinates(int universeSize, int allocationTryCount) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (int tryCount = 0; tryCount < allocationTryCount; tryCount++) {
            Coordinate coordinate = randomCoordinateProvider.getCoordinateInSquare(universeSize);

            if (notTooClose(coordinate, coordinates)) {
                coordinates.add(coordinate);
            } else {
                log.debug("Could not place system: Another one is too close.");
            }
        }
        return coordinates;
    }

    private boolean notTooClose(Coordinate coordinate, List<Coordinate> coordinates) {
        return coordinates.stream()
            .allMatch(c1 -> distanceCalculator.getDistance(c1, coordinate) >= properties.getSolarSystem().getMinSolarSystemDistance());
    }
}
