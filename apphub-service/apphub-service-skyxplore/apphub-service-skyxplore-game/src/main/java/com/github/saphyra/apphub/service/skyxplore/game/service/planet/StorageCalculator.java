package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling.DwellingBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StorageCalculator {
    private final StorageBuildingModuleService storageBuildingModuleService;
    private final DwellingBuildingService dwellingBuildingService;

    public int calculateStorageCapacity(GameData gameData, UUID location, StorageType storageType) {
        return gameData.getBuildingModules()
            .getByLocation(location)
            .stream()
            .filter(buildingModule -> storageBuildingModuleService.containsKey(buildingModule.getDataId())) //Filter for storage building module
            .filter(buildingModule -> gameData.getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty())
            .map(buildingModule -> storageBuildingModuleService.get(buildingModule.getDataId()).getStores())
            .filter(stores -> stores.containsKey(storageType)) //Check if storage building module can store the specific type
            .mapToInt(stores -> stores.get(storageType))
            .sum();
    }

    public int calculateDwellingCapacity(GameData gameData, UUID location){
        return gameData.getBuildingModules()
            .getByLocation(location)
            .stream()
            .filter(buildingModule -> dwellingBuildingService.containsKey(buildingModule.getDataId()))
            .filter(buildingModule -> gameData.getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty())
            .mapToInt(buildingModule -> dwellingBuildingService.get(buildingModule.getDataId()).getCapacity())
            .sum();
    }
}
