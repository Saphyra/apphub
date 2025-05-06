package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.UseAllocatedResourceService;
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
    private final StoredResourceConverter storedResourceConverter;
    private final ReservedStorageConverter reservedStorageConverter;

    UUID findProductionBuilding(GameData gameData, UUID location, String dataId) {
        return producerBuildingFinderService.findProducerBuildingId(gameData, location, dataId)
            .orElse(null);
    }

    void processResourceRequirements(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID location, String dataId, int amount, UUID producerBuildingModuleId) {
        String producerBuildingModuleDataId = gameData.getBuildingModules()
            .findByBuildingModuleIdValidated(producerBuildingModuleId)
            .getDataId();

        resourceRequirementProcessFactory.createResourceRequirementProcesses(progressDiff, gameData, processId, location, dataId, amount, producerBuildingModuleDataId)
            .forEach(productionOrderProcess -> {
                gameData.getProcesses()
                    .add(productionOrderProcess);
                progressDiff.save(productionOrderProcess.toModel());
            });
    }

    void startWork(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID producerBuildingModuleId, UUID reservedStorageId) {
        log.info("RequestWorkProcess is not present. Resolving allocations...");

        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByReservedStorageIdValidated(reservedStorageId);

        useAllocatedResourceService.resolveAllocations(progressDiff, gameData, reservedStorage.getLocation(), processId);

        String producerBuildingModuleDataId = gameData.getBuildingModules()
            .findByBuildingModuleIdValidated(producerBuildingModuleId)
            .getDataId();

        workProcessFactory.createForProduction(gameData, processId, reservedStorage.getLocation(), producerBuildingModuleDataId, reservedStorage.getDataId(), reservedStorage.getAmount())
            .forEach(requestWorkProcess -> {
                gameData.getProcesses()
                    .add(requestWorkProcess);
                progressDiff.save(requestWorkProcess.toModel());
            });
    }

    void storeResource(GameProgressDiff progressDiff, GameData gameData, UUID location, UUID reservedStorageId, UUID allocatedResourceId, int amount, UUID buildingModuleId) {
        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByReservedStorageIdValidated(reservedStorageId);
        log.info("ProductionOrder finished. Storing {} of {}", amount, reservedStorage.getDataId());

        StoredResource storedResource = gameData.getStoredResources()
            .findByLocationAndDataId(location, reservedStorage.getDataId())
            .orElseGet(() -> storedResourceFactory.create(progressDiff, gameData, location, reservedStorage.getDataId(), buildingModuleId, ContainerType.PRODUCER_OUTPUT));

        if (nonNull(allocatedResourceId)) {
            AllocatedResource allocatedResource = gameData.getAllocatedResources()
                .findByAllocatedResourceIdValidated(allocatedResourceId);

            allocatedResource.increaseAmount(amount);

            progressDiff.save(allocatedResourceConverter.toModel(gameData.getGameId(), allocatedResource));
        }
        storedResource.increaseAmount(amount);
        reservedStorage.decreaseAmount(amount);

        progressDiff.save(storedResourceConverter.toModel(gameData.getGameId(), storedResource));
        progressDiff.save(reservedStorageConverter.toModel(gameData.getGameId(), reservedStorage));
    }
}
