package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production.ProductionBuildingModuleDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.Production;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProductionProcessHelper {
    private final ProductionBuildingModuleDataService productionBuildingModuleDataService;
    private final WorkProcessFactory workProcessFactory;
    private final StorageCapacityService storageCapacityService;
    private final ResourceDataService resourceDataService;
    private final StoredResourceFactory storedResourceFactory;
    private final BuildingModuleService buildingModuleService;

    public void createWorkProcess(Game game, UUID location, UUID processId, UUID productionOrderId, int amount) {
        String resourceDataId = game.getData()
            .getProductionOrders()
            .findByIdValidated(productionOrderId)
            .getResourceDataId();
        Production production = productionBuildingModuleDataService.findProducerFor(resourceDataId)
            .getEntity2();
        int workPointsPerResource = production.getConstructionRequirements()
            .getRequiredWorkPoints();
        int workPointsNeeded = workPointsPerResource * amount;

        log.info("Created WorkProcesses for {} workPoints", workPointsNeeded);

        workProcessFactory.save(game, location, processId, workPointsNeeded, production.getRequiredSkill());
    }

    /**
     * @return true, if produced resources can be stored, false if storages are full.
     */
    public boolean resourcesProduced(Game game, UUID location, UUID productionOrderId, int amount) {
        ProductionOrder productionOrder = game.getData()
            .getProductionOrders()
            .findByIdValidated(productionOrderId);

        StorageType storageType = resourceDataService.get(productionOrder.getResourceDataId())
            .getStorageType();

        log.info("Storing {} resources to storage {}", amount, storageType);

        int availableCapacity = storageCapacityService.getEmptyConstructionAreaCapacity(game.getData(), productionOrder.getConstructionAreaId(), storageType);
        if (amount > availableCapacity) {
            log.info("{} capacity is needed, but there is only {} available.", amount, availableCapacity);
            return false;
        }

        Map<UUID, Integer> containers = getContainers(game.getData(), productionOrder.getConstructionAreaId(), productionOrder.getResourceDataId(), amount);
        log.info("<Container, Amount> mapping: {}", containers);

        containers.forEach((containerId, amountToStore) -> storedResourceFactory.save(
            game.getProgressDiff(),
            game.getData(),
            location,
            productionOrder.getResourceDataId(),
            amountToStore,
            containerId,
            ContainerType.STORAGE
        ));

        return true;
    }

    private Map<UUID, Integer> getContainers(GameData gameData, UUID constructionAreaId, String resourceDataId, int amount) {
        StorageType storageType = resourceDataService.get(resourceDataId)
            .getStorageType();

        //Fill the containers with the highest capacity first
        Queue<BiWrapper<UUID, Integer>> queue = new PriorityQueue<>((o1, o2) -> Integer.compare(o2.getEntity2(), o1.getEntity2()));

        buildingModuleService.getUsableContainers(gameData, constructionAreaId, storageType)
            .forEach(buildingModule -> queue.add(new BiWrapper<>(
                buildingModule.getBuildingModuleId(),
                storageCapacityService.getFreeContainerCapacity(gameData, buildingModule.getBuildingModuleId(), storageType)
            )));

        Map<UUID, Integer> result = new HashMap<>();
        while (amount > 0) {
            Optional<BiWrapper<UUID, Integer>> maybeContainer = Optional.ofNullable(queue.poll());
            if (maybeContainer.isEmpty()) {
                throw new IllegalStateException("There are no more containers to store " + amount + " of " + resourceDataId);
            }

            BiWrapper<UUID, Integer> container = maybeContainer.get();
            int toStore = Math.min(amount, container.getEntity2());

            result.put(container.getEntity1(), toStore);

            amount -= toStore;
        }

        return result;
    }
}
