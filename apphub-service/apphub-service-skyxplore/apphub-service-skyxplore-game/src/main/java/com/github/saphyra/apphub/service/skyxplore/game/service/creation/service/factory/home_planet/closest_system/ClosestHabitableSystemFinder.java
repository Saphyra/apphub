package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.closest_system;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ClosestHabitableSystemFinder {
    private final DistanceCalculator distanceCalculator;

    Optional<SolarSystem> findClosestHabitableSystem(SolarSystem solarSystem, Map<Coordinate, SolarSystem> systems, List<Line> connections) {
        return connections.stream()
            .sorted(Comparator.comparingDouble(o -> o.getLength(distanceCalculator)))
            .map(line -> line.getOtherEndpoint(solarSystem.getCoordinate()))
            .map(systems::get)
            .filter(this::hasEmptyPlanet)
            .findFirst();
    }

    private boolean hasEmptyPlanet(SolarSystem solarSystem) {
        return solarSystem.getPlanets()
            .values()
            .stream()
            .anyMatch(planet -> isNull(planet.getOwner()));
    }
}
