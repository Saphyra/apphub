package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RemovableConnectionFinder {
    private final DistanceCalculator distanceCalculator;
    private final ConnectionsOfConnectedSystemsCalculator connectionsOfConnectedSystemsCalculator;

    Line getRemovableLine(Coordinate system, List<Line> lines) {
        Map<Coordinate, Integer> connectedStars = connectionsOfConnectedSystemsCalculator.getNumberOfConnectionsOfConnectedSystems(system, lines);

        int maxNumberOfConnections = getMaxNumberOfConnections(connectedStars);

        List<Line> systemsWithMaxNumberOfConnections = getSystemsWithNumberOfConnections(system, lines, connectedStars, maxNumberOfConnections);

        return findLongestConnection(systemsWithMaxNumberOfConnections);
    }

    private int getMaxNumberOfConnections(Map<Coordinate, Integer> connectedStars) {
        int maxNumberOfConnections = connectedStars.values()
            .stream()
            .max(Integer::compareTo)
            .get();
        log.debug("Max number of connections: {}", maxNumberOfConnections);
        return maxNumberOfConnections;
    }

    private List<Line> getSystemsWithNumberOfConnections(Coordinate system, List<Line> lines, Map<Coordinate, Integer> connectedStars, int maxNumberOfConnections) {
        return connectedStars.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(maxNumberOfConnections))
            .map(Map.Entry::getKey)
            .map(coordinate -> findLine(lines, system, coordinate))
            .collect(Collectors.toList());
    }

    private Line findLine(List<Line> lines, Coordinate system, Coordinate coordinate) {
        return lines.stream()
            .filter(line -> line.isEndpoint(system) && line.isEndpoint(coordinate))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No line found for " + system + " and " + coordinate));
    }

    private Line findLongestConnection(List<Line> systemsWithMaxNumberOfConnections) {
        Line result = null;
        double longestLength = 0;
        for (Line line : systemsWithMaxNumberOfConnections) {
            double length = line.getLength(distanceCalculator);
            if (length > longestLength) {
                longestLength = length;
                result = line;
            }
        }

        return result;
    }
}
