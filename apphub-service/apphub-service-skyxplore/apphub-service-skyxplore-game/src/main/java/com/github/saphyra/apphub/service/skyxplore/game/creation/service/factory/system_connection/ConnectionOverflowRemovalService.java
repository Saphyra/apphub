package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class ConnectionOverflowRemovalService {
    private final GameCreationProperties properties;
    private final ConnectionCounter connectionCounter;
    private final RemovableConnectionFinder removableConnectionFinder;

    List<Line> removeConnectionOverflow(Collection<Coordinate> systems, List<Line> lines) {
        List<Line> result = new ArrayList<>(lines);

        int maxNumberOfConnections = properties.getSystemConnection()
            .getMaxNumberOfConnections();

        for (Coordinate system : systems) {
            log.debug("Checking if {} has too much connections...", system);
            for (int numberOfConnections = connectionCounter.getNumberOfConnections(system, result); numberOfConnections > maxNumberOfConnections; numberOfConnections = connectionCounter.getNumberOfConnections(system, result)) {
                log.debug("{} has {} number of connections. Removing one...", system, numberOfConnections);
                Line lineToRemove = removableConnectionFinder.getRemovableLine(system, result);
                log.debug("Removing connection {} because connected systems have too much connections.", lineToRemove);
                result.remove(lineToRemove);
            }
        }

        return result;
    }
}
