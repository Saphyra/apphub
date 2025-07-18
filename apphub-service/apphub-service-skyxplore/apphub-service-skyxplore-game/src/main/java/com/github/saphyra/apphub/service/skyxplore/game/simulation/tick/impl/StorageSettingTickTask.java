package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting.StorageSettingProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting.StorageSettingProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageSettingTickTask implements TickTask {
    private final StorageSettingProcessFactory storageSettingProcessFactory;
    private final StorageCapacityService storageCapacityService;
    private final BuildingModuleService buildingModuleService;
    private final ResourceDataService resourceDataService;

    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.STORAGE_SETTING;
    }

    @Override
    public void process(Game game) {
        game.getData()
            .getStorageSettings()
            .forEach(storageSetting -> createProductionOrderProcessIfNeeded(game, storageSetting));
    }

    private void createProductionOrderProcessIfNeeded(Game game, StorageSetting storageSetting) {
        GameData gameData = game.getData();

        log.info("Processing {}", storageSetting);

        int storedAmount = gameData.getStoredResources()
            .getByLocationAndDataId(storageSetting.getLocation(), storageSetting.getDataId())
            .stream()
            .mapToInt(StoredResource::getAmount)
            .sum();

        if (storedAmount >= storageSetting.getTargetAmount()) {
            log.info("There is enough {} at {}", storageSetting.getDataId(), storageSetting.getLocation());
            return;
        }

        int orderedAmount = gameData.getProcesses()
            .getByExternalReferenceAndType(storageSetting.getStorageSettingId(), ProcessType.STORAGE_SETTING)
            .stream()
            .map(process -> (StorageSettingProcess) process)
            .mapToInt(StorageSettingProcess::getAmount)
            .sum();

        if (orderedAmount + storedAmount >= storageSetting.getTargetAmount()) {
            log.info("Resources already ordered.");
            return;
        }

        int missingAmount = storageSetting.getTargetAmount() - orderedAmount;
        Map<UUID, Integer> containers = getContainers(gameData, storageSetting.getLocation(), storageSetting.getDataId(), missingAmount);
        log.info("Ordering {} of {} to containers {}", missingAmount, storageSetting.getDataId(), containers);

        containers.forEach((containerId, amount) -> storageSettingProcessFactory.save(game, storageSetting, containerId, amount));
    }

    private Map<UUID, Integer> getContainers(GameData gameData, UUID location, String dataId, int amount) {
        StorageType storageType = resourceDataService.get(dataId)
            .getStorageType();

        //Fill the containers with the highest capacity first
        Queue<BiWrapper<UUID, Integer>> queue = new PriorityQueue<>((o1, o2) -> Integer.compare(o2.getEntity2(), o1.getEntity2()));

        buildingModuleService.getUsableDepots(gameData, location, storageType)
            .map(buildingModule -> new BiWrapper<>(
                buildingModule.getBuildingModuleId(),
                storageCapacityService.getFreeContainerCapacity(gameData, buildingModule.getBuildingModuleId(), storageType)
            ))
            .filter(bw -> bw.getEntity2() > 0)
            .forEach(queue::add);

        Map<UUID, Integer> result = new HashMap<>();
        while (amount > 0) {
            BiWrapper<UUID, Integer> container = queue.poll();

            if (isNull(container)) {
                log.info("Not enough storage available for {} of {} at {}", amount, dataId, location);
                break;
            }

            int toStore = Math.min(amount, container.getEntity2());

            result.put(container.getEntity1(), toStore);

            amount -= toStore;
        }

        return result;
    }
}
