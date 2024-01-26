package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
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
import com.github.saphyra.apphub.service.skyxplore.game.util.HeadquartersUtil;
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
    private final HeadquartersUtil headquartersUtil;

    String findProductionBuilding(GameData gameData, UUID location, String dataId) {
        return producerBuildingFinderService.findProducerBuildingDataId(gameData, location, dataId)
            .orElseGet(() -> getHqIfSuitable(gameData, location, dataId));
    }

    private String getHqIfSuitable(GameData gameData, UUID location, String dataId) {
        if (gameData.getBuildings().getByLocationAndDataId(location, GameConstants.DATA_ID_HEADQUARTERS).isEmpty()) {
            log.debug("No Headquarters available on planet {}", location);
            return null;
        }

        if (headquartersUtil.getGives().contains(dataId)) {
            log.debug("Headquarters is able to produce {}", dataId);
            return GameConstants.DATA_ID_HEADQUARTERS;
        } else {
            log.debug("Headquarters is not able to produce {}", dataId);
            return null;
        }
    }

    void processResourceRequirements(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID location, String dataId, int amount, String producerBuildingDataId) {
        resourceRequirementProcessFactory.createResourceRequirementProcesses(progressDiff, gameData, processId, location, dataId, amount, producerBuildingDataId)
            .forEach(productionOrderProcess -> {
                gameData.getProcesses()
                    .add(productionOrderProcess);
                progressDiff.save(productionOrderProcess.toModel());
            });
    }

    void startWork(GameProgressDiff progressDiff, GameData gameData, UUID processId, String producerBuildingDataId, UUID reservedStorageId) {
        log.info("RequestWorkProcess is not present. Resolving allocations...");

        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByReservedStorageIdValidated(reservedStorageId);

        useAllocatedResourceService.resolveAllocations(progressDiff, gameData, reservedStorage.getLocation(), processId);

        workProcessFactory.createForProduction(gameData, processId, reservedStorage.getLocation(), producerBuildingDataId, reservedStorage.getDataId(), reservedStorage.getAmount())
            .forEach(requestWorkProcess -> {
                gameData.getProcesses()
                    .add(requestWorkProcess);
                progressDiff.save(requestWorkProcess.toModel());
            });
    }

    void storeResource(GameProgressDiff progressDiff, GameData gameData, UUID location, UUID reservedStorageId, UUID allocatedResourceId, int amount) {
        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByReservedStorageIdValidated(reservedStorageId);
        log.info("ProductionOrder finished. Storing {} of {}", amount, reservedStorage.getDataId());

        StoredResource storedResource = gameData.getStoredResources()
            .findByLocationAndDataId(location, reservedStorage.getDataId())
            .orElseGet(() -> storedResourceFactory.create(progressDiff, gameData, location, reservedStorage.getDataId()));

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
