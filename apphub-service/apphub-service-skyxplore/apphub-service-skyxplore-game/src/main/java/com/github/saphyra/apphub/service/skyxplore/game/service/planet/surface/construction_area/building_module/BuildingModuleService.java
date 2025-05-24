package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit tests
/*
Usable = Not under de/construction
 */
public class BuildingModuleService {
    private final StorageBuildingModuleDataService storageBuildingModuleDataService;

    public List<BuildingModule> getUsableContainers(GameData gameData, UUID constructionAreaId, StorageType storageType) {
        return gameData.getBuildingModules()
            .getByConstructionAreaId(constructionAreaId)
            .stream()
            .filter(buildingModule -> gameData.getConstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty())
            .filter(buildingModule -> gameData.getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty())
            .filter(buildingModule -> storageBuildingModuleDataService.containsKey(buildingModule.getDataId()))
            .filter(buildingModule -> storageBuildingModuleDataService.get(buildingModule.getDataId()).getStores().containsKey(storageType))
            .toList();
    }
}
