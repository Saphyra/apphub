package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.CrossCalculator;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SystemConnectionFactory {
    private final GameCreationProperties properties;
    private final IdGenerator idGenerator;
    private final DistanceCalculator distanceCalculator;
    private final CrossCalculator crossCalculator;

    public List<SystemConnection> create(Collection<Coordinate> systems) {
        Set<Line> lines = new HashSet<>();

        systems.forEach(coordinate -> lines.addAll(connectCloseStars(coordinate, systems)));
        lines.addAll(connectDistantStars(systems, lines));

        removeCrosses(lines);
        removeConnections(systems, lines);

        //TODO validate no dead system clusters

        return lines.stream()
            .map(line -> SystemConnection.builder().systemConnectionId(idGenerator.randomUuid()).line(line).build())
            .collect(Collectors.toList());
    }

    private List<Line> connectCloseStars(Coordinate system, Collection<Coordinate> systems) {
        return systems.stream()
            .filter(coordinate -> coordinate.equals(system))
            .filter(coordinate -> distanceCalculator.getDistance(coordinate, system).doubleValue() <= properties.getSystemConnection().getMaxDistance())
            .map(coordinate -> new Line(system, coordinate))
            .collect(Collectors.toList());
    }

    private List<Line> connectDistantStars(Collection<Coordinate> systems, Set<Line> lines) {
        if (systems.size() < 2) {
            return Collections.emptyList();
        }
        return systems.stream()
            .filter(system -> !hasConnection(system, lines))
            .map(system -> connectClosestStar(system, systems))
            .collect(Collectors.toList());
    }

    private boolean hasConnection(Coordinate system, Set<Line> lines) {
        return lines.stream()
            .noneMatch(line -> line.isEndpoint(system));
    }

    private Line connectClosestStar(Coordinate system, Collection<Coordinate> systems) {
        double minDistance = Integer.MAX_VALUE;
        Coordinate minSystem = null;

        for (Coordinate coordinate : systems) {
            double distance = distanceCalculator.getDistance(coordinate, system)
                .doubleValue();
            if (distance < minDistance) {
                minDistance = distance;
                minSystem = coordinate;
            }
        }

        return new Line(system, minSystem);
    }

    private void removeCrosses(Set<Line> lines) {
        while (true) {
            Optional<Cross> cross = getCross(lines);

            if (cross.isPresent()) {
                Line line1 = cross.get().getL1();
                Line line2 = cross.get().getL2();

                Pair<Line, Integer> l1AConnections = Pair.of(line1, getNumberOfConnections(line1.getA(), lines));
                Pair<Line, Integer> l1BConnections = Pair.of(line1, getNumberOfConnections(line1.getB(), lines));
                Pair<Line, Integer> l2AConnections = Pair.of(line2, getNumberOfConnections(line2.getA(), lines));
                Pair<Line, Integer> l2BConnections = Pair.of(line2, getNumberOfConnections(line2.getB(), lines));

                Line mostConnections = Stream.of(l1AConnections, l2AConnections, l1BConnections, l2BConnections)
                    .max(Comparator.comparing(Pair::getSecond))
                    .map(Pair::getFirst)
                    .get();
                lines.remove(mostConnections);
            } else {
                return;
            }
        }
    }

    private Optional<Cross> getCross(Set<Line> lines) {
        for (Line line1 : lines) {
            for (Line line2 : lines) {
                if (!line1.equals(line2)) {
                    Optional<Coordinate> crossPoint = crossCalculator.getCrossPointOfSections(line1, line2, false);
                    if (crossPoint.isPresent()) {
                        return Optional.of(new Cross(line1, line2));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private int getNumberOfConnections(Coordinate system, Set<Line> lines) {
        return (int) lines.stream()
            .filter(line -> line.isEndpoint(system))
            .count();
    }

    private void removeConnections(Collection<Coordinate> systems, Set<Line> lines) {
        for (Coordinate system : systems) {
            for (List<Line> connectionsOfStar = getConnectionsOfStar(system, lines); connectionsOfStar.size() < properties.getSystemConnection().getMaxNumberOfConnections(); connectionsOfStar = getConnectionsOfStar(system, lines)) {
                Line connectionToRemove = connectionsOfStar.stream()
                    .max(Comparator.comparingInt(line -> getNumberOfConnections(system, line, lines)))
                    .get();

                lines.remove(connectionToRemove);
            }
        }
    }

    private List<Line> getConnectionsOfStar(Coordinate system, Set<Line> lines) {
        return lines.stream()
            .filter(line -> line.isEndpoint(system))
            .collect(Collectors.toList());
    }

    private int getNumberOfConnections(Coordinate system, Line line, Set<Line> lines) {
        Coordinate coordinate = line.getA().equals(system) ? line.getB() : line.getA();

        return (int) lines.stream()
            .filter(l -> l.isEndpoint(coordinate))
            .count();
    }

    @Data
    @RequiredArgsConstructor
    private static class Cross {
        private final Line l1;
        private final Line l2;
    }
}
