package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting.StorageSettingProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting.StorageSettingProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageSettingTickTask implements TickTask {
    private final StorageSettingProcessFactory storageSettingProcessFactory;
    private final StorageCapacityService storageCapacityService;
    private final ResourceDataService resourceDataService;

    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.STORAGE_SETTING;
    }

    @Override
    public void process(Game game) {
        game.getData()
            .getStorageSettings()
            .forEach(storageSetting -> createProductionOrderProcessIfNeeded(game, game.getProgressDiff(), game.getData(), storageSetting));
    }

    private void createProductionOrderProcessIfNeeded(Game game, GameProgressDiff progressDiff, GameData gameData, StorageSetting storageSetting) {
        log.info("Processing {}", storageSetting);

        int storedAmount = gameData.getStoredResources()
            .getByLocationAndDataId(storageSetting.getLocation(), storageSetting.getDataId())
            .stream()
            .mapToInt(StoredResource::getAmount)
            .sum();

        if (storedAmount >= storageSetting.getTargetAmount()) {
            log.debug("There is enough {} at {}", storageSetting.getDataId(), storageSetting.getLocation());
            return;
        }

        int inProgressAmount = gameData.getProcesses()
            .getByExternalReferenceAndType(storageSetting.getStorageSettingId(), ProcessType.STORAGE_SETTING)
            .stream()
            .map(process -> (StorageSettingProcess) process)
            .mapToInt(StorageSettingProcess::getAmount)
            .sum();

        if (inProgressAmount + storedAmount >= storageSetting.getTargetAmount()) {
            log.debug("Resources already ordered.");
            return;
        }

        int missingAmount = storageSetting.getTargetAmount() - inProgressAmount;
        int freeStorage = storageCapacityService.getFreeDepotStorage(gameData, storageSetting.getLocation(), storageSetting.getDataId());
        if (freeStorage <= 0) {
            log.debug("Not enough storage");
            return;
        }

        int orderedAmount = Math.min(missingAmount, freeStorage);

        StorageSettingProcess process = storageSettingProcessFactory.create(game, storageSetting, orderedAmount);

        gameData.getProcesses()
            .add(process);

        progressDiff.save(process.toModel());
    }
}
