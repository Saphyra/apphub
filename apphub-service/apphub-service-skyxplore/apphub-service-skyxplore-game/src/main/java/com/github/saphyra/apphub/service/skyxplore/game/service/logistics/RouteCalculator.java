package com.github.saphyra.apphub.service.skyxplore.game.service.logistics;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinateFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouteCalculator {
    private static final List<Coordinate> MOVEMENTS = List.of(
        new Coordinate(0, 1),
        new Coordinate(1, 0),
        new Coordinate(0, -1),
        new Coordinate(-1, 0)
    );

    private final MapWeighter mapWeighter;
    private final ReferredCoordinateFactory referredCoordinateFactory;
    private final CoordinateFinder coordinateFinder;

    public List<ReferredCoordinate> calculateAndSaveForResourceDeliveryRequestId(GameProgressDiff progressDiff, GameData gameData, UUID location, UUID referenceId, UUID resourceDeliveryRequestId) {
        ResourceDeliveryRequest request = gameData.getResourceDeliveryRequests()
            .findByIdValidated(resourceDeliveryRequestId);
        StoredResource storedResource = gameData.getStoredResources()
            .findByAllocatedByValidated(resourceDeliveryRequestId);

        Coordinate startPoint = coordinateFinder.getCoordinateByStoredResource(gameData, storedResource);
        Coordinate endPoint = coordinateFinder.getCoordinateByReservedStorageId(gameData, request.getReservedStorageId());

        return calculateAndSaveRoute(progressDiff, gameData, location, referenceId, startPoint, endPoint);
    }

    private List<ReferredCoordinate> calculateAndSaveRoute(GameProgressDiff progressDiff, GameData gameData, UUID location, UUID referenceId, Coordinate startPoint, Coordinate endPoint) {
        List<Coordinate> shortestRoute = calculateRoute(gameData, location, startPoint, endPoint);

        List<ReferredCoordinate> result = new ArrayList<>();
        for (int i = 0; i < shortestRoute.size(); i++) {
            result.add(referredCoordinateFactory.save(progressDiff, gameData, referenceId, shortestRoute.get(i), i));
        }

        return result;
    }

    private List<Coordinate> calculateRoute(GameData gameData, UUID location, Coordinate startPoint, Coordinate endPoint) {
        log.info("Calculating route from {} to {}", startPoint, endPoint);
        Map<Coordinate, Integer> map = mapWeighter.getWeightedMap(gameData, location);
        map.forEach((coordinate, weight) -> log.debug("Weight of coordinate {}: {}", coordinate, weight));
        int size = Math.toIntExact(Math.round(Math.sqrt(map.size())));

        PriorityQueue<List<Coordinate>> queue = new PriorityQueue<>(Comparator.comparingInt(o -> o.stream().mapToInt(map::get).sum()));
        queue.offer(List.of(startPoint));
        List<Coordinate> shortestRoute = null;
        int shortestRouteWeight = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            List<Coordinate> route = queue.poll();
            int routeWeight = route.stream()
                .mapToInt(map::get)
                .sum();
            if (routeWeight < shortestRouteWeight) {
                Coordinate lastWaypoint = route.get(route.size() - 1);
                if (lastWaypoint.equals(endPoint)) {
                    shortestRoute = route;
                    shortestRouteWeight = routeWeight;
                } else {
                    MOVEMENTS.stream()
                        .map(lastWaypoint::add)
                        .filter(coordinate -> isCoordinateValid(coordinate, size))
                        .map(coordinate -> Stream.concat(route.stream(), Stream.of(coordinate)).toList())
                        .forEach(queue::offer);
                }
            }
        }

        if (isNull(shortestRoute) || shortestRoute.isEmpty()) {
            throw new IllegalStateException("Shortest route is too short: " + shortestRoute);
        }

        log.info("Route calculated: {}", shortestRoute);
        return shortestRoute;
    }

    private boolean isCoordinateValid(Coordinate coordinate, int size) {
        return coordinate.getX() >= 0 && coordinate.getX() < size
            && coordinate.getY() >= 0 && coordinate.getY() < size;
    }
}
