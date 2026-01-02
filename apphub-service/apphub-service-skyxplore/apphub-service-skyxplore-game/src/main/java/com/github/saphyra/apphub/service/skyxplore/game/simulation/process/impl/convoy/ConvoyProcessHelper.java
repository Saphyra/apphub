package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy_movement.ConvoyMovementProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConvoyProcessHelper {
    private final StoredResourceConverter storedResourceConverter;
    private final StoredResourceFactory storedResourceFactory;
    private final ConvoyMovementProcessFactory convoyMovementProcessFactory;
    private final GameProperties gameProperties;
    private final StorageCapacityService storageCapacityService;
    private final ReservedStorageConverter reservedStorageConverter;

    void releaseCitizen(GameProgressDiff progressDiff, GameData gameData, UUID processId) {
        gameData.getCitizenAllocations()
            .findByProcessId(processId)
            .ifPresent(citizenAllocation -> {
                gameData.getCitizenAllocations()
                    .remove(citizenAllocation);
                progressDiff.delete(citizenAllocation.getCitizenAllocationId(), GameItemType.CITIZEN_ALLOCATION);
            });
    }

    void loadResources(GameProgressDiff progressDiff, GameData gameData, UUID convoyId) {
        Convoy convoy = gameData.getConvoys()
            .findByIdValidated(convoyId);
        StoredResource source = gameData.getStoredResources()
            .findByAllocatedByValidated(convoy.getResourceDeliveryRequestId());
        int toDeliver = Math.min(convoy.getCapacity(), source.getAmount());

        extractFromSource(progressDiff, gameData, source, toDeliver);

        storedResourceFactory.save(progressDiff, gameData, source.getLocation(), source.getDataId(), toDeliver, convoyId, ContainerType.CONVOY);
    }

    private void extractFromSource(GameProgressDiff progressDiff, GameData gameData, StoredResource source, int toDeliver) {
        source.decreaseAmount(toDeliver);
        progressDiff.save(storedResourceConverter.toModel(gameData.getGameId(), source));
        log.info("Modified: {}", source);
    }

    /**
     * @return true, if the convoy reached its destination. False if it is still moving
     */
    boolean move(Game game, UUID location, UUID processId, UUID convoyId) {
        GameData gameData = game.getData();

        if (gameData.getProcesses().getByExternalReference(processId).stream().anyMatch(process -> process.getStatus() != ProcessStatus.DONE)) {
            log.info("Convoy is still moving");

            return false;
        }

        List<ReferredCoordinate> route = gameData.getCoordinates()
            .getByReferenceId(convoyId)
            .stream()
            .sorted(Comparator.comparingInt(ReferredCoordinate::getOrder))
            .toList();
        log.info("Route: {}", route);

        if (route.isEmpty()) {
            log.info("Convoy is arrived");
            return true;
        }

        UUID citizenId = gameData.getCitizenAllocations()
            .findByProcessIdValidated(processId)
            .getCitizenId();
        ReferredCoordinate waypoint = route.getFirst();
        int requiredWorkPoints = calculateRequiredWorkPoints(gameData, location, waypoint);

        convoyMovementProcessFactory.save(game, location, processId, citizenId, requiredWorkPoints);

        gameData.getCoordinates()
            .remove(waypoint);
        game.getProgressDiff()
            .delete(waypoint.getReferredCoordinateId(), GameItemType.COORDINATE);

        return false;
    }

    private int calculateRequiredWorkPoints(GameData gameData, UUID location, ReferredCoordinate referredCoordinate) {
        SurfaceType surfaceType = gameData.getSurfaces()
            .getByPlanetId(location)
            .stream()
            .filter(surface -> gameData.getCoordinates().findByReferenceIdValidated(surface.getSurfaceId()).equals(referredCoordinate.getCoordinate()))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Surface not found on planet " + location + " with coordinate " + referredCoordinate.getCoordinate()))
            .getSurfaceType();

        Integer weight = gameProperties.getSurface()
            .getLogisticsWeight()
            .get(surfaceType);

        return weight * gameProperties.getLogisticsWeightMultiplier();
    }

    boolean unloadResources(GameProgressDiff progressDiff, GameData gameData, UUID convoyId) {
        StoredResource resource = gameData.getStoredResources()
            .findByContainerIdValidated(convoyId);
        Convoy convoy = gameData.getConvoys()
            .findByIdValidated(convoyId);
        ResourceDeliveryRequest deliveryRequest = gameData.getResourceDeliveryRequests()
            .findByIdValidated(convoy.getResourceDeliveryRequestId());
        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByIdValidated(deliveryRequest.getReservedStorageId());

        int freeCapacity = storageCapacityService.getEmptyContainerCapacity(gameData, reservedStorage.getContainerId(), reservedStorage.getContainerType(), reservedStorage.getDataId());
        if (freeCapacity < resource.getAmount()) {
            log.info("There is not enough capacity in storage {} to deposit {} of {}", resource.getContainerId(), resource.getAmount(), resource.getDataId());
            return false;
        }

        storedResourceFactory.save(
            progressDiff,
            gameData,
            resource.getLocation(),
            resource.getDataId(),
            resource.getAmount(),
            reservedStorage.getContainerId(),
            reservedStorage.getContainerType(),
            reservedStorage.getExternalReference()
        );
        reservedStorage.decreaseAmount(resource.getAmount());
        log.info("Modified: {}", reservedStorage);

        //Resource needed for cleanup / cancel processes
        resource.setAmount(0);

        progressDiff.save(storedResourceConverter.toModel(gameData.getGameId(), resource));
        progressDiff.save(reservedStorageConverter.toModel(gameData.getGameId(), reservedStorage));

        return true;
    }

    void cleanup(GameProgressDiff progressDiff, GameData gameData, UUID convoyId) {
        gameData.getStoredResources()
            .getByContainerId(convoyId)
            .forEach(storedResource -> {
                progressDiff.delete(storedResource.getStoredResourceId(), GameItemType.STORED_RESOURCE);
                gameData.getStoredResources()
                    .remove(storedResource);
            });

        gameData.getConvoys()
            .remove(convoyId);
        progressDiff.delete(convoyId, GameItemType.CONVOY);
    }
}
