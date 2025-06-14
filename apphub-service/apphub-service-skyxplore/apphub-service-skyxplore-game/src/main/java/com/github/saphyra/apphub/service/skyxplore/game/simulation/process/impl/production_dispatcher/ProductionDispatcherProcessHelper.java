package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_dispatcher;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production.ProductionBuildingModuleDataService;
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
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order.ProductionOrderProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProductionDispatcherProcessHelper {
    private final ProductionBuildingModuleDataService productionBuildingModuleDataService;
    private final StorageCapacityService storageCapacityService;
    private final GameProperties gameProperties;
    private final ResourceDataService resourceDataService;
    private final ProductionOrderFactory productionOrderFactory;
    private final ProductionOrderProcessFactory productionOrderProcessFactory;
    private final ProductionRequestConverter productionRequestConverter;

    public int dispatch(Game game, UUID location, UUID processId, UUID productionRequestId, int missingAmount) {
        ProductionRequest productionRequest = game.getData()
            .getProductionRequests()
            .findByIdValidated(productionRequestId);

        String resourceDataId = game.getData()
            .getReservedStorages()
            .findByIdValidated(productionRequest.getReservedStorageId())
            .getDataId();

        Queue<BiWrapper<UUID, Integer>> eligibleConstructionAreas = getEligibleConstructionAreas(game.getData(), location, productionRequest.getProductionRequestId(), resourceDataId);
        log.info("Eligible constructionAreas: {}", eligibleConstructionAreas);

        int dispatched = 0;
        while (missingAmount > 0) {
            BiWrapper<UUID, Integer> constructionArea = eligibleConstructionAreas.poll();
            if (isNull(constructionArea)) {
                break;
            }

            int toDispatch = Math.min(missingAmount, constructionArea.getEntity2());

            dispatchToConstructionArea(game, location, processId, productionRequest, constructionArea.getEntity1(), resourceDataId, constructionArea.getEntity2());

            dispatched += toDispatch;
            missingAmount -= toDispatch;
        }

        productionRequest.increaseDispatchedAmount(dispatched);
        game.getProgressDiff()
            .save(productionRequestConverter.toModel(game.getGameId(), productionRequest));

        return dispatched;
    }

    private void dispatchToConstructionArea(Game game, UUID location, UUID processId, ProductionRequest productionRequest, UUID constructionAreaId, String resourceDataId, int amount) {
        log.info("Dispatching {} of {} to constructionArea {}", amount, resourceDataId, constructionAreaId);
        ProductionOrder productionOrder = productionOrderFactory.save(game.getProgressDiff(), game.getData(), productionRequest.getProductionRequestId(), constructionAreaId, resourceDataId, amount);

        productionOrderProcessFactory.save(game, location, processId, productionOrder.getProductionOrderId());
    }

    //TODO check if suitable storage for all requiredResources in constructionRequirements
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

        StorageType storageType = resourceDataService.get(resourceDataId)
            .getStorageType();

        int totalCapacity = storageCapacityService.getTotalConstructionAreaCapacity(gameData, constructionAreaId, storageType);
        int storedAmount = storageCapacityService.getOccupiedConstructionAreaCapacity(gameData, constructionAreaId, storageType);

        int result = (int) Math.floor((totalCapacity - storedAmount) * gameProperties.getProduction().getProductionOrderMaxDispatchedRatio());

        log.info("ConstructionArea {} has {} {} capacity total, {} is used. Max availability: {}", constructionAreaId, totalCapacity, storageType, storedAmount, result);

        return result;
    }

    private Queue<BiWrapper<UUID, Integer>> getEligibleConstructionAreas(GameData gameData, UUID location, UUID productionRequestId, String resourceDataId) {
        log.info("Searching for producers can produce {} at location {}", resourceDataId, location);
        Queue<BiWrapper<UUID, Integer>> result = new PriorityQueue<>((o1, o2) -> Integer.compare(o2.getEntity2(), o1.getEntity2()));

        gameData.getBuildingModules()
            .getByLocation(location)
            .stream()
            .filter(buildingModule -> productionBuildingModuleDataService.containsKey(buildingModule.getDataId()))
            //TODO filter under construction/deconstruction
            .filter(buildingModule -> canProduce(resourceDataId, buildingModule.getDataId()))
            .map(BuildingModule::getConstructionAreaId)
            .distinct()
            .map(constructionAreaId -> new BiWrapper<>(constructionAreaId, calculateAvailability(gameData, productionRequestId, constructionAreaId, resourceDataId)))
            .filter(mapping -> mapping.getEntity2() > 0)
            .forEach(result::add);

        return result;
    }

    private boolean canProduce(String resourceDataId, String buildingModuleDataId) {
        log.debug("Checking if {} can produce {}", buildingModuleDataId, resourceDataId);
        return productionBuildingModuleDataService.get(buildingModuleDataId)
            .getProduces()
            .stream()
            .anyMatch(production -> production.getResourceDataId().equals(resourceDataId));
    }
}
