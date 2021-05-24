package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.closest_system;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClosestSystemFinder {
    private final DistanceCalculator distanceCalculator;
    private final WaypointCandidateFilter waypointCandidateFilter;
    private final ClosestHabitableSystemFinder closestHabitableSystemFinder;
    private final NextWaypointSelector nextWaypointSelector;

    public SolarSystem getClosestSystemWithEmptyPlanet(SolarSystem solarSystem, Universe universe) {
        return getClosestSystemWithEmptyPlanet(solarSystem, universe, Arrays.asList(solarSystem.getCoordinate()))
            .getEntity1();
    }

    BiWrapper<SolarSystem, Double> getClosestSystemWithEmptyPlanet(SolarSystem solarSystem, Universe universe, List<Coordinate> route) {
        List<Line> connections = waypointCandidateFilter.getWayPointCandidates(solarSystem, universe, route);

        Optional<SolarSystem> closestSystemWithEmptyPlanetOptional = closestHabitableSystemFinder.findClosestHabitableSystem(solarSystem, universe.getSystems(), connections);

        if (closestSystemWithEmptyPlanetOptional.isPresent()) {
            SolarSystem closestSystemWithEmptyPlanet = closestSystemWithEmptyPlanetOptional.get();
            List<Coordinate> newRoute = new ArrayList<>(route);
            newRoute.add(closestSystemWithEmptyPlanet.getCoordinate());
            return BiWrapper.<SolarSystem, Double>builder()
                .entity1(closestSystemWithEmptyPlanet)
                .entity2(distanceCalculator.getDistance(newRoute))
                .build();
        } else {
            return nextWaypointSelector.findNextWaypoint(solarSystem, universe, route, connections, this);
        }
    }
}
