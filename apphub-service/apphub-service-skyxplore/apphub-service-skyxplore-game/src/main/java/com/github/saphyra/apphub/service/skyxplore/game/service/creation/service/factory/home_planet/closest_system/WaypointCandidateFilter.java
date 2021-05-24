package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.closest_system;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class WaypointCandidateFilter {
    List<Line> getWayPointCandidates(SolarSystem solarSystem, Universe universe, List<Coordinate> route) {
        return universe.getConnections()
            .stream()
            .map(SystemConnection::getLine)
            .filter(systemConnection -> systemConnection.isEndpoint(solarSystem.getCoordinate()))
            .filter(systemConnection -> !route.contains(systemConnection.getA()) && !route.contains(systemConnection.getB()))
            .collect(Collectors.toList());
    }
}
