package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.CrossCalculator;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SystemConnectionFactory {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(8);

    private final GameCreationProperties properties;
    private final IdGenerator idGenerator;
    private final DistanceCalculator distanceCalculator;
    private final CrossCalculator crossCalculator;
    private final SleepService sleepService;

    public List<SystemConnection> create(Collection<Coordinate> systems) {
        log.info("Generating connections...");
        Set<Line> lines = new HashSet<>();

        List<Future<?>> futures = systems.stream()
            .map(coordinate -> EXECUTOR.submit(() -> lines.addAll(connectToAllSystems(coordinate, systems))))
            .collect(Collectors.toList());

        boolean allReady;
        do {
            allReady = futures.stream()
                .allMatch(Future::isDone);

            if (!allReady) {
                log.info("Connection generation is not done.");
                sleepService.sleep(1000);
            }
        } while (!allReady);
        log.info("Number of connections after connection all stars to each other: {}", lines.size());

        log.info("Removing crosses...");
        removeCrosses(lines);
        log.info("Number of connections after removing crosses: {}", lines.size());

        log.info("Removing connections too close to a system...");
        removeLinesTooCloseToASystem(systems, lines);
        log.info("Number of connections after removing the ones too close to a system: {}", lines.size());

        log.info("Removing connections from systems with too much connections...");
        removeConnectionOverflow(systems, lines);

        log.info("Number of connections remaining: {}", lines.size());
        return lines.stream()
            .peek(line -> log.debug("Connection: {}", line))
            .map(line -> SystemConnection.builder().systemConnectionId(idGenerator.randomUuid()).line(line).build())
            .collect(Collectors.toList());
    }

    private Collection<Line> connectToAllSystems(Coordinate system, Collection<Coordinate> systems) {
        log.debug("Generating connections for coordinate {}", system);
        Set<Line> result = new HashSet<>();

        systems.stream()
            .filter(coordinate -> !coordinate.equals(system))
            .forEach(coordinate -> result.add(new Line(system, coordinate)));

        return result;
    }


    private void removeCrosses(Set<Line> lines) {
        int crossAmount = 0;
        for (Optional<Cross> optionalCross = findCross(lines); optionalCross.isPresent(); optionalCross = findCross(lines)) {
            Cross cross = optionalCross.get();

            Line line1 = cross.getLine1();
            Line line2 = cross.getLine2();
            Line lineToRemove = Stream.of(line1, line2)
                .max(Comparator.comparing(o -> o.getLength(distanceCalculator)))
                .orElseThrow(() -> new RuntimeException("This should not happen"));
            log.debug("Removing connection {}  due to cross with {}", lineToRemove, cross.getOther(lineToRemove));

            lines.remove(lineToRemove);
            crossAmount++;
        }
        log.info("{} number of crosses were removed.", crossAmount);
    }

    private Optional<Cross> findCross(Set<Line> lines) {
        for (Line line1 : lines) {
            for (Line line2 : lines) {
                if (line1.equals(line2)) {
                    continue;
                }
                Optional<Coordinate> crossPoints = crossCalculator.getCrossPointOfSections(line1, line2, false);
                if (crossPoints.isPresent()) {
                    Cross cross = new Cross(line1, line2);
                    log.debug("Cross found: {}", cross);
                    return Optional.of(cross);
                }
            }
        }

        return Optional.empty();
    }

    private void removeLinesTooCloseToASystem(Collection<Coordinate> systems, Set<Line> lines) {
        Set<Line> linesToRemove = systems.stream()
            .flatMap(coordinate -> getTooCloseSystems(coordinate, lines))
            .collect(Collectors.toSet());

        linesToRemove.forEach(lines::remove);
        log.info("{} number of close connections were removed.", linesToRemove.size());
    }

    private Stream<Line> getTooCloseSystems(Coordinate coordinate, Set<Line> lines) {
        return lines.stream()
            .filter(line -> isTooClose(coordinate, line, lines));
    }

    boolean isTooClose(Coordinate coordinate, Line line, Collection<Line> lines) {
        if (line.isEndpoint(coordinate)) {
            log.debug("{} is endpoint of {}", coordinate, line);
            return false;
        }

        if (!isTriangle(coordinate, line, lines)) {
            log.debug("{} has no connection with both endpoints of {}", coordinate, line);
            return false;
        }

        double height = distanceCalculator.getDistance(coordinate, line);
        double aDist = distanceCalculator.getDistance(coordinate, line.getA());
        double bDist = distanceCalculator.getDistance(coordinate, line.getB());

        double aAngle = Math.acos(height / aDist);
        double bAngle = Math.acos(height / bDist);

        double angleRad = aAngle + bAngle;
        double angle = angleRad * 180 / Math.PI;

        boolean result = angle > 150;
        log.debug("{}, {} - height: {}, angle: {}", coordinate, line, height, angle);
        if (result) {
            log.debug("Removing connection {} because it is too close to system {}. Distance: {}, angle: {}", line, coordinate, height, angle);
        }
        return result;
    }

    private boolean isTriangle(Coordinate coordinate, Line line, Collection<Line> lines) {
        return hasConnection(coordinate, line.getA(), lines) && hasConnection(coordinate, line.getB(), lines);
    }

    private boolean hasConnection(Coordinate a, Coordinate b, Collection<Line> lines) {
        return lines.stream()
            .anyMatch(line -> line.isEndpoint(a) && line.isEndpoint(b));
    }

    private void removeConnectionOverflow(Collection<Coordinate> systems, Set<Line> lines) {
        for (Coordinate system : systems) {
            log.debug("Checking if {} has too much connections...", system);
            for (int numberOfConnections = getNumberOfConnections(system, lines); numberOfConnections > properties.getSystemConnection().getMaxNumberOfConnections(); numberOfConnections = getNumberOfConnections(system, lines)) {
                log.debug("{} has {} number of connections. Removing one...", system, numberOfConnections);
                Line lineToRemove = getRemovableLine(system, lines);
                log.debug("Removing connection {} because connected systems have too much connections.", lineToRemove);
                lines.remove(lineToRemove);
            }
        }
    }

    private Line getRemovableLine(Coordinate system, Set<Line> lines) {
        Map<Coordinate, Integer> connectedStars = lines.stream()
            .filter(line -> line.isEndpoint(system))
            .map(line -> line.getOtherEndpoint(system))
            .collect(Collectors.toMap(Function.identity(), coordinate -> (int) getNumberOfConnections(coordinate, lines)));

        int maxNumberOfConnections = connectedStars.values()
            .stream()
            .max(Integer::compareTo)
            .get();
        log.debug("Max number of connections: {}", maxNumberOfConnections);

        List<Line> systemsWithMaxNumberOfConnections = connectedStars.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(maxNumberOfConnections))
            .map(Map.Entry::getKey)
            .map(coordinate -> findLine(lines, system, coordinate))
            .collect(Collectors.toList());

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

    private Line findLine(Set<Line> lines, Coordinate system, Coordinate coordinate) {
        return lines.stream()
            .filter(line -> line.isEndpoint(system) && line.isEndpoint(coordinate))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No line found for " + system + " and " + coordinate));
    }

    private int getNumberOfConnections(Coordinate system, Set<Line> lines) {
        int result = (int) lines.stream()
            .filter(line -> line.isEndpoint(system))
            .count();
        log.debug("{} has {} number of connections", system, result);
        return result;
    }

    @AllArgsConstructor
    @Data
    private static class Cross {
        private final Line line1;
        private final Line line2;

        public Line getOther(Line line) {
            return line.equals(line1) ? line2 : line1;
        }
    }
}
