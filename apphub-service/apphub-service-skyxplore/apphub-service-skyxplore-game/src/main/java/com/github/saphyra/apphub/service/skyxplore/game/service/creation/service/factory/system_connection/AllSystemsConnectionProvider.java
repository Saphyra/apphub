package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class AllSystemsConnectionProvider {
    List<Line> connectToAllSystems(Coordinate system, Collection<Coordinate> systems) {
        log.debug("Generating connections for coordinate {}", system);

        return systems.stream()
            .filter(coordinate -> !coordinate.equals(system))
            .map(coordinate -> new Line(system, coordinate))
            .collect(Collectors.toList());
    }
}
