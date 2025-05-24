package com.github.saphyra.apphub.service.skyxplore.game.service.logistics;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinateFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO split
public class RouteCalculator {
    private static final List<Coordinate> MOVEMENTS = List.of(
        new Coordinate(0, 1),
        new Coordinate(1, 0),
        new Coordinate(0, -1),
        new Coordinate(-1, 0)
    );

    private final GameProperties gameProperties;
    private final ReferredCoordinateFactory referredCoordinateFactory;

    public List<ReferredCoordinate> calculateForResourceDeliveryRequestId(GameProgressDiff progressDiff, GameData gameData, UUID location, UUID referenceId, UUID resourceDeliveryRequestId) {
        ResourceDeliveryRequest request = gameData.getResourceDeliveryRequests()
            .findByIdValidated(resourceDeliveryRequestId);
        StoredResource storedResource = gameData.getStoredResources()
            .findByAllocatedByValidated(request.getResourceDeliveryRequestId());

        Coordinate startPoint = getCoordinateByStoredResource(gameData, storedResource);
        Coordinate endPoint = getCoordinateByReservedStorageId(gameData, request.getReservedStorageId());

        return calculateRoute(progressDiff, gameData, location, referenceId, startPoint, endPoint);
    }

    private List<ReferredCoordinate> calculateRoute(GameProgressDiff progressDiff, GameData gameData, UUID location, UUID referenceId, Coordinate startPoint, Coordinate endPoint) {
        Map<Coordinate, Integer> map = getWeightedMap(gameData, location);
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

        if(isNull(shortestRoute) || shortestRoute.isEmpty()){
            throw new IllegalStateException("Shortest route is too short: " + shortestRoute);
        }

        List<ReferredCoordinate> result = new ArrayList<>();
        for(int i = 0; i < shortestRoute.size(); i++){
            result.add(referredCoordinateFactory.save(progressDiff, gameData, referenceId, shortestRoute.get(i), i));
        }

        return result;
    }

    private boolean isCoordinateValid(Coordinate coordinate, int size) {
        return coordinate.getX() >= 0 && coordinate.getX() <= size
            && coordinate.getY() >= 0 && coordinate.getY() <= size;
    }

    private Map<Coordinate, Integer> getWeightedMap(GameData gameData, UUID location) {
        return gameData.getSurfaces()
            .getByPlanetId(location)
            .stream()
            .collect(Collectors.toMap(surface -> gameData.getCoordinates().findByReferenceId(surface.getSurfaceId()), surface -> gameProperties.getSurface().getLogisticsWeight().get(surface.getSurfaceType())));
    }

    private Coordinate getCoordinateByStoredResource(GameData gameData, StoredResource storedResource) {
        return getCoordinateByIdAndContainerType(gameData, storedResource.getContainerId(), storedResource.getContainerType());
    }

    private Coordinate getCoordinateByReservedStorageId(GameData gameData, UUID reservedStorageId) {
        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByIdValidated(reservedStorageId);

        return getCoordinateByIdAndContainerType(gameData, reservedStorage.getContainerId(), reservedStorage.getContainerType());
    }

    private Coordinate getCoordinateByIdAndContainerType(GameData gameData, UUID containerId, ContainerType containerType) {
        return switch (containerType) {
            case STORAGE -> getCoordinateByBuildingModuleId(gameData, containerId);
            case SURFACE -> getCoordinateBySurfaceId(gameData, containerId);
            case CONSTRUCTION_AREA -> getCoordinateByConstructionAreaId(gameData, containerId);
            default -> throw ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, "Unhandled ContainerType: " + containerId);
        };
    }

    private Coordinate getCoordinateByBuildingModuleId(GameData gameData, UUID buildingModuleId) {
        UUID constructionAreaId = gameData.getBuildingModules()
            .findByIdValidated(buildingModuleId)
            .getConstructionAreaId();

        return getCoordinateByConstructionAreaId(gameData, constructionAreaId);
    }

    private Coordinate getCoordinateByConstructionAreaId(GameData gameData, UUID constructionAreaId) {
        UUID surfaceId = gameData.getConstructionAreas()
            .findByIdValidated(constructionAreaId)
            .getSurfaceId();

        return getCoordinateBySurfaceId(gameData, surfaceId);
    }

    private Coordinate getCoordinateBySurfaceId(GameData gameData, UUID surfaceId) {
        return gameData.getCoordinates()
            .findByReferenceId(surfaceId);
    }
}
