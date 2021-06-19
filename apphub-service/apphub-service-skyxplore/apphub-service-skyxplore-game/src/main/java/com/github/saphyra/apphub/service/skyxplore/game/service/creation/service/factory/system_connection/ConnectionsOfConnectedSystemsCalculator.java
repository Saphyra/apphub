package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class ConnectionsOfConnectedSystemsCalculator {
    private final ConnectionCounter connectionCounter;

    Map<Coordinate, Integer> getNumberOfConnectionsOfConnectedSystems(Coordinate system, List<Line> lines) {
        return lines.stream()
            .filter(line -> line.isEndpoint(system))
            .map(line -> line.getOtherEndpoint(system))
            .collect(Collectors.toMap(Function.identity(), coordinate -> (int) connectionCounter.getNumberOfConnections(coordinate, lines)));
    }
}
