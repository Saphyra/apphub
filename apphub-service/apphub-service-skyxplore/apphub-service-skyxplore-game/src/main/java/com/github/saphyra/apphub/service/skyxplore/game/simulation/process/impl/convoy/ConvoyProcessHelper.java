package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
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
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
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

        source.decreaseAmount(toDeliver);
        progressDiff.save(storedResourceConverter.toModel(gameData.getGameId(), source));

        storedResourceFactory.save(progressDiff, gameData, source.getLocation(), source.getDataId(), toDeliver, convoyId, ContainerType.CONVOY);
    }

    /**
     * @return true, if the convoy reached its destination. False if it is still moving
     */
    public boolean move(Game game, UUID location, UUID processId, UUID convoyId) {
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

        if (route.isEmpty()) {
            log.info("Convoy is arrived");
            return true;
        }

        UUID citizenId = gameData.getCitizenAllocations()
            .findByProcessIdValidated(processId)
            .getCitizenId();
        int requiredWorkPoints = calculateRequiredWorkPoints(gameData, route.get(0));

        convoyMovementProcessFactory.save(game, location, processId, citizenId, requiredWorkPoints);

        return false;
    }

    private int calculateRequiredWorkPoints(GameData gameData, ReferredCoordinate referredCoordinate) {
        SurfaceType surfaceType = gameData.getSurfaces()
            .findByIdValidated(referredCoordinate.getReferenceId())
            .getSurfaceType();

        return gameProperties.getSurface()
            .getLogisticsWeight()
            .get(surfaceType);
    }

    public boolean unloadResources(GameProgressDiff progressDiff, GameData gameData, UUID convoyId) {
        StoredResource resource = gameData.getStoredResources()
            .findByAllocatedByValidated(convoyId);
        Convoy convoy = gameData.getConvoys()
            .findByIdValidated(convoyId);
        ResourceDeliveryRequest deliveryRequest = gameData.getResourceDeliveryRequests()
            .findByIdValidated(convoy.getResourceDeliveryRequestId());
        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByIdValidated(deliveryRequest.getReservedStorageId());

        int freeCapacity = storageCapacityService.getEmptyContainerCapacity(gameData, reservedStorage.getContainerId(), reservedStorage.getDataId());
        if (freeCapacity < resource.getAmount()) {
            log.info("There is not enough capacity in storage {} to deposit {} of {}", resource.getContainerId(), resource.getAmount(), resource.getDataId());
            return false;
        }

        storedResourceFactory.save(progressDiff, gameData, resource.getLocation(), resource.getDataId(), resource.getAmount(), reservedStorage.getContainerId(), reservedStorage.getContainerType());
        reservedStorage.decreaseAmount(reservedStorage.getAmount());

        //Resource needed for cleanup / cancel processes
        resource.setAmount(0);

        progressDiff.save(storedResourceConverter.toModel(gameData.getGameId(), resource));
        progressDiff.save(reservedStorageConverter.toModel(gameData.getGameId(), reservedStorage));

        return true;
    }

    public void cleanup(GameProgressDiff progressDiff, GameData gameData, UUID convoyId) {
        UUID surfaceId = gameData.getCoordinates()
            .getByReferenceId(convoyId)
            .stream()
            .sorted(Comparator.comparingInt(ReferredCoordinate::getOrder))
            .toList()
            .get(0)
            .getReferenceId();

        gameData.getStoredResources()
            .getByContainerId(convoyId)
            .forEach(storedResource -> {
                storedResource.setContainerId(surfaceId);
                storedResource.setContainerType(ContainerType.SURFACE);

                progressDiff.save(storedResourceConverter.toModel(gameData.getGameId(), storedResource));
            });

        gameData.getConvoys()
            .remove(convoyId);
        progressDiff.delete(convoyId, GameItemType.CONVOY);
    }
}
