package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production.ProductionBuildingModuleDataService;
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
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceAggregator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production.ProductionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production.ProductionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request.ResourceRequestProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionOrderProcessHelper {
    private final ProductionBuildingModuleDataService productionBuildingModuleDataService;
    private final ReservedStorageFactory reservedStorageFactory;
    private final ResourceRequestProcessFactory resourceRequestProcessFactory;
    private final ResourceDataService resourceDataService;
    private final StoredResourceConverter storedResourceConverter;
    private final ProductionOrderConverter productionOrderConverter;
    private final ProductionProcessFactory productionProcessFactory;
    private final BuildingModuleAllocationFactory buildingModuleAllocationFactory;
    private final BuildingModuleService buildingModuleService;
    private final StoredResourceAggregator storedResourceAggregator;

    void orderResources(Game game, UUID location, UUID processId, UUID productionOrderId) {
        GameData gameData = game.getData();

        ProductionOrder productionOrder = gameData.getProductionOrders()
            .findByIdValidated(productionOrderId);

        getRequiredResources(productionOrder)
            .forEach((resourceDataId, amountPerResource) -> {
                int amount = amountPerResource * productionOrder.getRequestedAmount();

                log.info("Ordering {} of {} to work on {}", amount, resourceDataId, productionOrder);
                UUID containerId = getContainerId(gameData, productionOrder.getConstructionAreaId(), resourceDataId);
                ReservedStorage reservedStorage = reservedStorageFactory.save(
                    game.getProgressDiff(),
                    gameData,
                    containerId,
                    ContainerType.STORAGE,
                    processId,
                    resourceDataId,
                    amount
                );

                resourceRequestProcessFactory.save(game, location, processId, reservedStorage.getReservedStorageId());
            });
    }

    private Map<String, Integer> getRequiredResources(ProductionOrder productionOrder) {
        return productionBuildingModuleDataService.findProducerFor(productionOrder.getResourceDataId())
            .getEntity2()
            .getConstructionRequirements()
            .getRequiredResources();
    }

    private UUID getContainerId(GameData gameData, UUID constructionAreaId, String resourceDataId) {
        StorageType storageType = resourceDataService.get(resourceDataId)
            .getStorageType();

        return buildingModuleService.getUsableConstructionAreaContainers(gameData, constructionAreaId, storageType)
            .findAny()
            .map(BuildingModule::getBuildingModuleId)
            .orElseThrow(() -> ExceptionFactory.reportedException("There is no storage that can store " + storageType + " in constructionArea " + constructionAreaId));
    }

    void tryProduce(Game game, UUID location, UUID processId, UUID productionOrderId) {
        GameData gameData = game.getData();

        ProductionOrder productionOrder = gameData.getProductionOrders()
            .findByIdValidated(productionOrderId);

        log.info("Attempting production of {}", productionOrder);

        Optional<UUID> maybeProducerBuildingModuleId = buildingModuleService.getUsableConstructionAreaProducers(gameData, productionOrder.getConstructionAreaId(), productionOrder.getResourceDataId())
            .map(BuildingModule::getBuildingModuleId)
            .filter(buildingModuleId -> gameData.getBuildingModuleAllocations().findByBuildingModuleId(buildingModuleId).isEmpty())
            .findAny();

        log.info("Available producer: {}", maybeProducerBuildingModuleId);

        if (maybeProducerBuildingModuleId.isEmpty()) {
            log.info("There is no available producer for {} on constructionArea {}", productionOrder.getResourceDataId(), productionOrder.getConstructionAreaId());
            return;
        }

        Map<String, List<StoredResource>> availableResources = gameData.getStoredResources()
            .getByAllocatedBy(processId)
            .stream()
            .collect(Collectors.groupingBy(StoredResource::getDataId));

        Map<String, Integer> resourceMap = availableResources.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().mapToInt(StoredResource::getAmount).sum()));
        log.info("Available resources: {}", resourceMap);

        int missingAmount = productionOrder.getMissingAmount();
        Map<String, Integer> constructionRequirements = getRequiredResources(productionOrder);
        log.info("ConstructionRequirements: {}", constructionRequirements);

        int amountToStart = calculateAmountToStart(resourceMap, constructionRequirements, missingAmount);
        log.info("{} resources can be produced from the available resources.", amountToStart);

        if (amountToStart == 0) {
            log.info("There is not enough resources available to start production of {} on constructionArea {}", productionOrder.getResourceDataId(), productionOrder.getConstructionAreaId());
            return;
        }

        useRequiredResources(game, availableResources, constructionRequirements, amountToStart);

        productionOrder.increaseStartedAmount(amountToStart);
        game.getProgressDiff()
            .save(productionOrderConverter.toModel(game.getGameId(), productionOrder));

        UUID buildingModuleId = maybeProducerBuildingModuleId.get();
        ProductionProcess process = productionProcessFactory.save(game, location, processId, productionOrderId, buildingModuleId, amountToStart);

        buildingModuleAllocationFactory.save(game.getProgressDiff(), game.getData(), buildingModuleId, process.getProcessId());
        log.info("Started working on {} resources.", amountToStart);
    }

    private void useRequiredResources(Game game, Map<String, List<StoredResource>> availableResources, Map<String, Integer> constructionRequirements, int amountToStart) {
        constructionRequirements.forEach((resourceDataId, amountNeeded) -> {
            StoredResource storedResource = storedResourceAggregator.aggregate(game.getProgressDiff(), game.getData(), availableResources.get(resourceDataId));

            storedResource.decreaseAmount(amountToStart * amountNeeded);

            game.getProgressDiff()
                .save(storedResourceConverter.toModel(game.getGameId(), storedResource));
        });
    }

    private int calculateAmountToStart(Map<String, Integer> resourceMap, Map<String, Integer> constructionRequirements, int missingAmount) {
        int result = missingAmount;

        for (Map.Entry<String, Integer> required : constructionRequirements.entrySet()) {
            Integer available = resourceMap.getOrDefault(required.getKey(), 0);
            int enoughFor = available / required.getValue();
            log.info("There is {} of {} available, enough for {} resources. Required for one: {}, total needed: {}", available, required.getKey(), enoughFor, required.getValue(), missingAmount);

            if (enoughFor < result) {
                result = Math.min(enoughFor, missingAmount);
            }
        }

        return result;
    }
}