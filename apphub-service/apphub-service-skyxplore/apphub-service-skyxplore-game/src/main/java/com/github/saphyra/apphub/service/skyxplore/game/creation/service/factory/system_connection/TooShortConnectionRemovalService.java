package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class TooShortConnectionRemovalService {
    private final DistanceCalculator distanceCalculator;
    private final TriangleConnectionFinder triangleConnectionFinder;

    List<Line> filterLinesTooCloseToASystem(Collection<Coordinate> systems, Collection<Line> lines) {
        Set<Line> linesToRemove = systems.stream()
            .flatMap(coordinate -> getTooCloseSystems(coordinate, lines))
            .collect(Collectors.toSet());

        log.info("{} number of close connections were removed.", linesToRemove.size());
        return lines.stream()
            .filter(line -> !linesToRemove.contains(line))
            .collect(Collectors.toList());
    }

    private Stream<Line> getTooCloseSystems(Coordinate coordinate, Collection<Line> lines) {
        return lines.stream()
            .filter(line -> isTooClose(coordinate, line, lines));
    }

    boolean isTooClose(Coordinate coordinate, Line line, Collection<Line> lines) {
        if (line.isEndpoint(coordinate)) {
            log.debug("{} is endpoint of {}", coordinate, line);
            return false;
        }

        if (!triangleConnectionFinder.isTriangle(coordinate, line, lines)) {
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
}
