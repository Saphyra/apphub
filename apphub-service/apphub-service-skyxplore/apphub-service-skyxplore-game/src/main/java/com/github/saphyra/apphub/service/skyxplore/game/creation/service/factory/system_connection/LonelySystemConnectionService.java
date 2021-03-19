package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class LonelySystemConnectionService {
    private final ConnectionCounter connectionCounter;
    private final DistanceCalculator distanceCalculator;

    List<Line> connectLonelySystems(Collection<Coordinate> systems, List<Line> lines) {
        List<Line> result = new ArrayList<>(lines);

        for (Optional<Coordinate> c = getLonelySystem(systems, result); c.isPresent(); c = getLonelySystem(systems, result)) {
            result.add(connectToClosestSystem(c.get(), systems));
        }

        return result;
    }

    private Optional<Coordinate> getLonelySystem(Collection<Coordinate> systems, List<Line> lines) {
        return systems.stream()
            .filter(coordinate -> connectionCounter.getNumberOfConnections(coordinate, lines) == 0)
            .findAny();
    }

    private Line connectToClosestSystem(Coordinate coordinate, Collection<Coordinate> systems) {
        Coordinate closestSystem = systems.stream()
            .filter(c -> !coordinate.equals(c))
            .min((o1, o2) -> (int) (distanceCalculator.getDistance(coordinate, o1) - distanceCalculator.getDistance(coordinate, o2)))
            .get();
        return new Line(coordinate, closestSystem);
    }
}
