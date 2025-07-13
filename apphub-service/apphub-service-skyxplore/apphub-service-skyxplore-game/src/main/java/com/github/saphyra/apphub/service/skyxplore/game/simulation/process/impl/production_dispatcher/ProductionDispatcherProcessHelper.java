package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_dispatcher;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrderFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequestConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order.ProductionOrderProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionDispatcherProcessHelper {
    private final StorageCapacityService storageCapacityService;
    private final GameProperties gameProperties;
    private final ResourceDataService resourceDataService;
    private final ProductionOrderFactory productionOrderFactory;
    private final ProductionOrderProcessFactory productionOrderProcessFactory;
    private final ProductionRequestConverter productionRequestConverter;
    private final BuildingModuleService buildingModuleService;

    public int dispatch(Game game, UUID location, UUID processId, UUID productionRequestId, int missingAmount) {
        ProductionRequest productionRequest = game.getData()
            .getProductionRequests()
            .findByIdValidated(productionRequestId);

        String resourceDataId = game.getData()
            .getReservedStorages()
            .findByIdValidated(productionRequest.getReservedStorageId())
            .getDataId();

        Queue<BiWrapper<UUID, Integer>> eligibleConstructionAreas = getEligibleConstructionAreas(game.getData(), location, productionRequestId, resourceDataId);
        log.info("Eligible constructionAreas: {}", eligibleConstructionAreas);

        int dispatched = 0;
        while (missingAmount > 0) {
            BiWrapper<UUID, Integer> constructionArea = eligibleConstructionAreas.poll();
            if (isNull(constructionArea)) {
                break;
            }

            int toDispatch = Math.min(missingAmount, constructionArea.getEntity2());

            dispatchToConstructionArea(game, location, processId, productionRequest, constructionArea.getEntity1(), resourceDataId, toDispatch);

            dispatched += toDispatch;
            missingAmount -= toDispatch;
        }

        if (dispatched > 0) {
            productionRequest.increaseDispatchedAmount(dispatched);
            game.getProgressDiff()
                .save(productionRequestConverter.toModel(game.getGameId(), productionRequest));
        }

        return dispatched;
    }

    private void dispatchToConstructionArea(Game game, UUID location, UUID processId, ProductionRequest productionRequest, UUID constructionAreaId, String resourceDataId, int amount) {
        log.info("Dispatching {} of {} to constructionArea {}", amount, resourceDataId, constructionAreaId);
        ProductionOrder productionOrder = productionOrderFactory.save(game.getProgressDiff(), game.getData(), productionRequest.getProductionRequestId(), constructionAreaId, resourceDataId, amount);

        productionOrderProcessFactory.save(game, location, processId, productionOrder.getProductionOrderId());
    }

    private Queue<BiWrapper<UUID, Integer>> getEligibleConstructionAreas(GameData gameData, UUID location, UUID productionRequestId, String resourceDataId) {
        log.info("Searching for producers can produce {} at location {}", resourceDataId, location);
        Queue<BiWrapper<UUID, Integer>> result = new PriorityQueue<>((o1, o2) -> Integer.compare(o2.getEntity2(), o1.getEntity2()));

        buildingModuleService.getProducersOf(gameData, location, resourceDataId)
            .map(BuildingModule::getConstructionAreaId)
            .distinct()
            .map(constructionAreaId -> new BiWrapper<>(constructionAreaId, calculateAvailability(gameData, productionRequestId, constructionAreaId, resourceDataId)))
            .filter(mapping -> mapping.getEntity2() > 0)
            .forEach(result::add);

        return result;
    }

    private Integer calculateAvailability(GameData gameData, UUID productionRequestId, UUID constructionAreaId, String resourceDataId) {
        Optional<ProductionOrder> maybeProductionOrderInProgress = gameData.getProductionOrders()
            .getByProductionRequestIdAndConstructionAreaIdAndResourceDataId(productionRequestId, constructionAreaId, resourceDataId)
            .stream()
            .filter(productionOrder -> !productionOrder.allStarted())
            .findAny();
        log.info("Scheduled productionOrder: {}", maybeProductionOrderInProgress);

        if (maybeProductionOrderInProgress.isPresent()) {
            log.info("{} production is already scheduled on constructionArea {} for productionRequestId {}", resourceDataId, constructionAreaId, productionRequestId);
            return 0;
        }

        Collection<StorageType> requirementStorageTypes = resourceDataService.get(resourceDataId)
            .getConstructionRequirements()
            .getRequiredResources()
            .keySet()
            .stream()
            .map(rd -> resourceDataService.get(rd).getStorageType())
            .collect(Collectors.toSet());

        StorageType storageType = resourceDataService.get(resourceDataId)
            .getStorageType();

        if (!hasRequiredTypesOfStorage(gameData, constructionAreaId, Stream.concat(Stream.of(storageType), requirementStorageTypes.stream()).collect(Collectors.toSet()))) {
            log.info("ConstructionArea {} does not have the required storage types for {}", constructionAreaId, resourceDataId);
            return 0;
        }

        int totalCapacity = storageCapacityService.getTotalConstructionAreaCapacity(gameData, constructionAreaId, storageType);
        int storedAmount = storageCapacityService.getOccupiedConstructionAreaCapacity(gameData, constructionAreaId, storageType);
        double maxDispatchedRatio = gameProperties.getProduction()
            .getProductionOrderMaxDispatchedRatio();

        int result = (int) Math.floor((totalCapacity - storedAmount) * maxDispatchedRatio);

        log.info("ConstructionArea {} has {} {} capacity total, {} is used. Max availability: {}", constructionAreaId, totalCapacity, storageType, storedAmount, result);

        return result;
    }

    private boolean hasRequiredTypesOfStorage(GameData gameData, UUID constructionAreaId, Set<StorageType> requiredStorageTypes) {
        Collection<StorageType> availableStorageTypes = buildingModuleService.getAvailableStorageTypes(gameData, constructionAreaId)
            .toList();

        return availableStorageTypes.containsAll(requiredStorageTypes);
    }
}
