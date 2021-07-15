package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.closest_system;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class NextWaypointSelector {
    BiWrapper<SolarSystem, Double> findNextWaypoint(SolarSystem solarSystem, Universe universe, List<Coordinate> route, List<Line> connections, ClosestSystemFinder closestSystemFinder) {
        return connections.stream()
            .map(line -> line.getOtherEndpoint(solarSystem.getCoordinate().getCoordinate()))
            .map(coordinate -> {
                List<Coordinate> newRoute = new ArrayList<>(route);
                newRoute.add(coordinate);
                return closestSystemFinder.getClosestSystemWithEmptyPlanet(universe.getSystems().get(coordinate), universe, newRoute);
            }).min(Comparator.comparingDouble(BiWrapper::getEntity2))
            .orElseThrow(() -> new IllegalStateException("No empty planet left."));
    }
}
