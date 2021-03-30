package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
class TriangleConnectionFinder {
    boolean isTriangle(Coordinate coordinate, Line line, Collection<Line> lines) {
        return hasConnection(coordinate, line.getA(), lines) && hasConnection(coordinate, line.getB(), lines);
    }

    private boolean hasConnection(Coordinate a, Coordinate b, Collection<Line> lines) {
        return lines.stream()
            .anyMatch(line -> line.isEndpoint(a) && line.isEndpoint(b));
    }
}
