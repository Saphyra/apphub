package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.CrossCalculator;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        Context context = new Context(this);

        log.info("Generating connections...");
        Set<Line> lines = new HashSet<>();

        systems
            .stream()
            .parallel()
            .forEach(coordinate -> lines.addAll(connectToAllSystems(coordinate, systems, context)));

        log.info("Number of generated connections: {}", lines.size());
        return lines.stream()
            .peek(line -> log.info("Connection: {}", line))
            .map(line -> SystemConnection.builder().systemConnectionId(idGenerator.randomUuid()).line(line).build())
            .collect(Collectors.toList());
    }

    private Collection<Line> connectToAllSystems(Coordinate system, Collection<Coordinate> systems, Context context) {
        Set<Line> result = new HashSet<>();

        for (Coordinate target : systems) {
            if (!system.equals(target)) {
                result.addAll(findBestPath(system, target, systems, context));
            }
        }

        return result;
    }

    private Collection<Line> findBestPath(Coordinate system, Coordinate target, Collection<Coordinate> systems, Context context) {
        List<Coordinate> currentPath = new ArrayList<>();
        currentPath.add(system);
        List<List<Coordinate>> routes = getRoutes(system, target, systems, currentPath, context);

        return routes.stream()
            .map(this::toLines)
            .min(Comparator.comparing(this::getPathDistance))
            .orElseThrow(() -> new RuntimeException("No path available from " + system + " to " + target));
    }

    private Double getPathDistance(List<Line> path) {
        return path.stream()
            .mapToDouble(line -> line.getLength(distanceCalculator))
            .reduce(0, Double::sum);
    }

    private List<Line> toLines(List<Coordinate> coordinates) {
        List<Line> result = new ArrayList<>();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            result.add(new Line(coordinates.get(i), coordinates.get(i + 1)));
        }

        return result;
    }

    private List<List<Coordinate>> getRoutes(Coordinate system, Coordinate target, Collection<Coordinate> systems, List<Coordinate> currentPath, Context context) {
        List<List<Coordinate>> result = new ArrayList<>();

        List<Coordinate> systemsWithinRange = getSystemsInRangeExcept(system, systems, currentPath, context);
        if (systemsWithinRange.isEmpty()) {

            Optional<Coordinate> closestAvailableSystem = getClosestSystemExcept(system, systems, currentPath, context);
            systemsWithinRange = closestAvailableSystem
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
        }

        for (Coordinate coordinate : systemsWithinRange) {
            List<Coordinate> newPath = new ArrayList<>(currentPath);
            newPath.add(coordinate);
            if (coordinate.equals(target)) {
                result.add(newPath);
            } else {
                List<List<Coordinate>> partialResult = getRoutes(coordinate, target, systems, newPath, context);
                result.addAll(partialResult);
            }
        }

        return result;
    }

    private List<Coordinate> getSystemsInRangeExcept(Coordinate system, Collection<Coordinate> systems, List<Coordinate> currentPath, Context context) {
        return systems.stream()
            .filter(coordinate -> !currentPath.contains(coordinate))
            .filter(coordinate -> context.isInRange(system, coordinate))
            .collect(Collectors.toList());
    }

    private Optional<Coordinate> getClosestSystemExcept(Coordinate system, Collection<Coordinate> systems, List<Coordinate> currentPath, Context context) {
        List<Coordinate> availableSystems = systems.stream()
            .filter(coordinate -> !currentPath.contains(coordinate))
            .collect(Collectors.toList());

        if (availableSystems.isEmpty()) {
            return Optional.empty();
        }

        double minDistance = Double.MAX_VALUE;
        Coordinate minSystem = null;

        for (Coordinate coordinate : availableSystems) {
            double distance = context.getDistanceBetween(system, coordinate);
            if (distance < minDistance) {
                minDistance = distance;
                minSystem = coordinate;
            }
        }

        return Optional.of(minSystem);
    }

    @RequiredArgsConstructor
    private static class Context {
        private final SystemConnectionFactory systemConnectionFactory;

        private final List<Line> lineCache = new ArrayList<>();

        public boolean isInRange(Coordinate c1, Coordinate c2) {


            return getDistanceBetween(c1, c2) <= systemConnectionFactory.properties.getSystemConnection().getMaxDistance();
        }

        public double getDistanceBetween(Coordinate c1, Coordinate c2) {
            Line line = new Line(c1, c2);
            int index = lineCache.indexOf(line);

            double length;
            if (index < 0) {
                length = line.getLength(systemConnectionFactory.distanceCalculator);
                lineCache.add(line);
            } else {
                length = lineCache.get(index).getLength();
            }
            return length;
        }
    }
}
