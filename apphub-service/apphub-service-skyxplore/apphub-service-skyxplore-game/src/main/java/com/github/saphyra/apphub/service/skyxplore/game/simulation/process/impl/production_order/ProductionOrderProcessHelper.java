package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production.ProductionBuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production.ProductionBuildingModuleDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrderConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production.ProductionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production.ProductionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request.ResourceRequestProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionOrderProcessHelper {
    private final ProductionBuildingModuleDataService productionBuildingModuleDataService;
    private final ReservedStorageFactory reservedStorageFactory;
    private final ResourceRequestProcessFactory resourceRequestProcessFactory;
    private final ResourceDataService resourceDataService;
    private final StorageBuildingModuleDataService storageBuildingModuleDataService;
    private final StoredResourceConverter storedResourceConverter;
    private final ProductionOrderConverter productionOrderConverter;
    private final ProductionProcessFactory productionProcessFactory;
    private final BuildingModuleAllocationFactory buildingModuleAllocationFactory;

    void orderResources(Game game, UUID location, UUID processId, UUID productionOrderId) {
        GameData gameData = game.getData();

        ProductionOrder productionOrder = gameData
            .getProductionOrders()
            .findByIdValidated(productionOrderId);

        getRequiredResources(gameData, productionOrder)
            .forEach((resourceDataId, amountPerResource) -> {
                ReservedStorage reservedStorage = reservedStorageFactory.save(
                    game.getProgressDiff(),
                    gameData,
                    getContainerId(gameData, productionOrder.getConstructionAreaId(), resourceDataId),
                    ContainerType.STORAGE,
                    processId,
                    resourceDataId,
                    amountPerResource * productionOrder.getRequestedAmount()
                );

                resourceRequestProcessFactory.save(game, location, processId, reservedStorage.getReservedStorageId());
            });
    }

    private Map<String, Integer> getRequiredResources(GameData gameData, ProductionOrder productionOrder) {
        return productionBuildingModuleDataService.get(getProductionBuildingModuleDataId(gameData, productionOrder))
            .getConstructionRequirements()
            .getRequiredResources();
    }

    private String getProductionBuildingModuleDataId(GameData gameData, ProductionOrder productionOrder) {
        return gameData.getBuildingModules()
            .getByConstructionAreaId(productionOrder.getConstructionAreaId())
            .stream()
            .filter(buildingModule -> productionBuildingModuleDataService.containsKey(buildingModule.getDataId()))
            .map(buildingModule -> productionBuildingModuleDataService.get(buildingModule.getDataId()))
            .filter(productionBuildingModuleData -> canProduce(productionBuildingModuleData, productionOrder.getResourceDataId()))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.reportedException("No producer available for " + productionOrder.getResourceDataId() + " on ConstructionArea " + productionOrder.getConstructionAreaId()))
            .getId();
    }

    private UUID getContainerId(GameData gameData, UUID constructionAreaId, String resourceDataId) {
        StorageType storageType = resourceDataService.get(resourceDataId)
            .getStorageType();

        return gameData.getBuildingModules()
            .getByConstructionAreaId(constructionAreaId)
            .stream()
            .filter(buildingModule -> storageBuildingModuleDataService.containsKey(buildingModule.getDataId()))
            //TODO filter under de/construction
            .filter(buildingModule -> storageBuildingModuleDataService.get(buildingModule.getDataId()).getStores().containsKey(storageType))
            .findAny()
            .map(BuildingModule::getBuildingModuleId)
            .orElseThrow(() -> ExceptionFactory.reportedException("There is no storage that can store " + storageType + " in constructionArea " + constructionAreaId));
    }

    private boolean canProduce(ProductionBuildingModuleData buildingModuleData, String resourceDataId) {
        return buildingModuleData.getProduces()
            .stream()
            .anyMatch(production -> production.getResourceDataId().equals(resourceDataId));
    }

    public void tryProduce(Game game, UUID location, UUID processId, UUID productionOrderId) {
        GameData gameData = game.getData();

        ProductionOrder productionOrder = gameData.getProductionOrders()
            .findByIdValidated(productionOrderId);

        Optional<UUID> maybeProducerBuildingModuleId = gameData.getBuildingModules()
            .getByConstructionAreaId(productionOrder.getConstructionAreaId())
            .stream()
            .filter(buildingModule -> productionBuildingModuleDataService.containsKey(buildingModule.getDataId()))
            //TODO filter under de/construction
            .filter(buildingModule -> canProduce(productionBuildingModuleDataService.get(buildingModule.getDataId()), productionOrder.getResourceDataId()))
            .map(BuildingModule::getBuildingModuleId)
            .filter(buildingModuleId -> gameData.getBuildingModuleAllocations().findByBuildingModuleId(buildingModuleId).isEmpty())
            .findAny();

        if (maybeProducerBuildingModuleId.isEmpty()) {
            log.info("There is no available producer for {} on constructionArea {}", productionOrder.getResourceDataId(), productionOrder.getConstructionAreaId());
            return;
        }

        Map<String, StoredResource> availableResources = gameData.getStoredResources()
            .getByAllocatedBy(processId)
            .stream()
            .collect(Collectors.toMap(StoredResource::getDataId, Function.identity()));

        Map<String, Integer> resourceMap = availableResources.values()
            .stream()
            .collect(Collectors.toMap(StoredResource::getDataId, StoredResource::getAmount));

        int missingAmount = productionOrder.getMissingAmount();
        Map<String, Integer> constructionRequirements = getRequiredResources(gameData, productionOrder);

        int amountToStart = calculateAmountToStart(resourceMap, constructionRequirements, missingAmount);
        if (amountToStart == 0) {
            log.info("There is not enough resources available to start production of {} on constructionArea {}", productionOrder.getResourceDataId(), productionOrder.getConstructionAreaId());
        }

        constructionRequirements.forEach((resourceDataId, amountNeeded) -> {
            StoredResource storedResource = availableResources.get(resourceDataId);

            storedResource.decreaseAmount(amountToStart * amountNeeded);

            game.getProgressDiff()
                .save(storedResourceConverter.toModel(game.getGameId(), storedResource));
        });

        productionOrder.increaseStartedAmount(amountToStart);
        game.getProgressDiff()
            .save(productionOrderConverter.toModel(game.getGameId(), productionOrder));

        UUID buildingModuleId = maybeProducerBuildingModuleId.get();
        ProductionProcess process = productionProcessFactory.save(game, location, processId, productionOrderId, buildingModuleId, amountToStart);

        buildingModuleAllocationFactory.save(game.getProgressDiff(), game.getData(), buildingModuleId, process.getProcessId());
    }

    private int calculateAmountToStart(Map<String, Integer> resourceMap, Map<String, Integer> constructionRequirements, int missingAmount) {
        int result = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> required : constructionRequirements.entrySet()) {
            Integer available = resourceMap.getOrDefault(required.getKey(), 0);
            int enoughFor = available / (missingAmount * required.getValue());
            log.info("There is {} of {} available, enough for {} resources.", available, required.getKey(), enoughFor);

            if (enoughFor < result) {
                result = Math.min(enoughFor, missingAmount);
            }
        }

        return result;
    }
}