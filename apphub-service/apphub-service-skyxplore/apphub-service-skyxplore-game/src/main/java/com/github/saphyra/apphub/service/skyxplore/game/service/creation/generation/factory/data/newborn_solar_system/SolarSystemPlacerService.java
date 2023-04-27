package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemPlacerService {
    private final GameProperties gameProperties;
    private final Random random;
    private final DistanceCalculator distanceCalculator;
    private final SolarSystemShifter solarSystemShifter;

    Map<Coordinate, UUID[]> place(List<UUID[]> solarSystems) {
        Map<Coordinate, UUID[]> placedSolarSystems = new HashMap<>();

        solarSystems.forEach(solarSystem -> placedSolarSystems.put(place(placedSolarSystems), solarSystem));

        return solarSystemShifter.shiftSolarSystems(placedSolarSystems);
    }

    private Coordinate place(Map<Coordinate, UUID[]> placedSolarSystems) {
        if (placedSolarSystems.isEmpty()) {
            return GameConstants.ORIGO;
        } else {
            while (true) {
                Optional<Coordinate> maybeCoordinate = tryPlace(placedSolarSystems);
                if (maybeCoordinate.isPresent()) {
                    return maybeCoordinate.get();
                }
            }
        }
    }

    private Optional<Coordinate> tryPlace(Map<Coordinate, UUID[]> placedSolarSystems) {
        Range<Integer> distance = gameProperties.getSolarSystem()
            .getSolarSystemDistance();

        Coordinate reference = getReferenceCoordinate(placedSolarSystems);

        int dx = random.randInt(distance) * (random.randBoolean() ? 1 : -1);
        int dy = random.randInt(distance) * (random.randBoolean() ? 1 : -1);

        Coordinate newCoordinate = new Coordinate(reference.getX() + dx, reference.getY() + dy);

        return Optional.of(newCoordinate)
            .filter(coordinate -> isFarEnough(newCoordinate, placedSolarSystems.keySet()));
    }

    private Coordinate getReferenceCoordinate(Map<Coordinate, UUID[]> placedSolarSystems) {
        List<Coordinate> coordinates = new ArrayList<>(placedSolarSystems.keySet());

        return coordinates.get(random.randInt(0, coordinates.size() - 1));
    }

    private boolean isFarEnough(Coordinate newCoordinate, Set<Coordinate> keySet) {
        return keySet.stream()
            .allMatch(coordinate -> distanceCalculator.getDistance(newCoordinate, coordinate) > gameProperties.getSolarSystem().getSolarSystemDistance().getMin());
    }
}
