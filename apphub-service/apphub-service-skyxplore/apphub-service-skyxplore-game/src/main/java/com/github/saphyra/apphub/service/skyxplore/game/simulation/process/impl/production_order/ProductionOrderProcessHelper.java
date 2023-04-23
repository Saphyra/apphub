package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.UseAllocatedResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionOrderProcessHelper {
    private final ProducerBuildingFinderService producerBuildingFinderService;
    private final ResourceRequirementProcessFactory resourceRequirementProcessFactory;
    private final UseAllocatedResourceService useAllocatedResourceService;
    private final WorkProcessFactory workProcessFactory;
    private final StoredResourceFactory storedResourceFactory;
    private final AllocatedResourceConverter allocatedResourceConverter;

    String findProductionBuilding(GameData gameData, UUID location, String dataId) {
        return producerBuildingFinderService.findProducerBuildingDataId(gameData, location, dataId)
            .orElse(null);
    }

    void processResourceRequirements(SyncCache syncCache, GameData gameData, UUID processId, UUID location, String dataId, int amount, String producerBuildingDataId) {
        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        resourceRequirementProcessFactory.createResourceRequirementProcesses(syncCache, gameData, processId, location, ownerId, dataId, amount, producerBuildingDataId)
            .forEach(productionOrderProcess -> {
                gameData.getProcesses()
                    .add(productionOrderProcess);
                syncCache.saveGameItem(productionOrderProcess.toModel());
            });
    }

    void startWork(SyncCache syncCache, GameData gameData, UUID processId, String producerBuildingDataId, UUID reservedStorageId) {
        log.info("RequestWorkProcess is not present. Resolving allocations...");

        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByReservedStorageIdValidated(reservedStorageId);

        UUID ownerId = gameData.getPlanets()
            .get(reservedStorage.getLocation())
            .getOwner();

        useAllocatedResourceService.resolveAllocations(syncCache, gameData, reservedStorage.getLocation(), ownerId, processId);

        workProcessFactory.createForProduction(gameData, processId, reservedStorage.getLocation(), producerBuildingDataId, reservedStorage.getDataId(), reservedStorage.getAmount())
            .forEach(requestWorkProcess -> {
                gameData.getProcesses()
                    .add(requestWorkProcess);
                syncCache.saveGameItem(requestWorkProcess.toModel());
            });
    }

    void storeResource(SyncCache syncCache, GameData gameData, UUID location, UUID reservedStorageId, UUID allocatedResourceId, int amount) {
        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByReservedStorageIdValidated(reservedStorageId);
        log.info("ProductionOrder finished. Storing {} of {}", amount, reservedStorage.getDataId());

        StoredResource storedResource = gameData.getStoredResources()
            .findByLocationAndDataId(location, reservedStorage.getDataId())
            .orElseGet(() -> storedResourceFactory.create(syncCache, gameData, location, reservedStorage.getDataId()));

        if (nonNull(allocatedResourceId)) {
            AllocatedResource allocatedResource = gameData.getAllocatedResources()
                .findByAllocatedResourceIdValidated(allocatedResourceId);

            allocatedResource.increaseAmount(amount);

            syncCache.saveGameItem(allocatedResourceConverter.toModel(gameData.getGameId(), allocatedResource));
        }
        storedResource.increaseAmount(amount);
        reservedStorage.decreaseAmount(amount);

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        syncCache.resourceStored(ownerId, location, storedResource, reservedStorage);
    }
}
