package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling.DwellingBuildingDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants.DEPOT_BUILDING_MODULE_CATEGORIES;

@Component
@RequiredArgsConstructor
@Slf4j
/*
Usable = Not under de/construction
 */
public class BuildingModuleService {
    private final StorageBuildingModuleDataService storageBuildingModuleDataService;
    private final DwellingBuildingDataService dwellingBuildingDataService;

    public Stream<BuildingModule> getUsableConstructionAreaContainers(GameData gameData, UUID constructionAreaId, StorageType storageType) {
        return gameData.getBuildingModules()
            .getByConstructionAreaId(constructionAreaId)
            .stream()
            .filter(buildingModule -> gameData.getConstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty())
            .filter(buildingModule -> gameData.getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty())
            .filter(buildingModule -> storageBuildingModuleDataService.containsKey(buildingModule.getDataId()))
            .filter(buildingModule -> storageBuildingModuleDataService.get(buildingModule.getDataId()).getStores().containsKey(storageType));
    }

    public Stream<BuildingModule> getUsableDepots(GameData gameData, UUID location, StorageType storageType) {
        return getDepots(gameData, location)
            .filter(buildingModule -> gameData.getConstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty())
            .filter(buildingModule -> gameData.getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty())
            .filter(buildingModule -> storageBuildingModuleDataService.get(buildingModule.getDataId()).getStores().containsKey(storageType));
    }

    public Stream<BuildingModule> getDepots(GameData gameData, UUID location) {
        return gameData.getBuildingModules()
            .getByLocation(location)
            .stream()
            .filter(buildingModule -> storageBuildingModuleDataService.containsKey(buildingModule.getDataId()))
            .filter(buildingModuleData -> DEPOT_BUILDING_MODULE_CATEGORIES.contains(storageBuildingModuleDataService.get(buildingModuleData.getDataId()).getCategory()));
    }

    public Stream<BuildingModule> getUsableDwelling(GameData gameData, UUID location) {
        return gameData.getBuildingModules()
            .getByLocation(location)
            .stream()
            .filter(buildingModule -> dwellingBuildingDataService.containsKey(buildingModule.getDataId()))
            .filter(buildingModule -> gameData.getConstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty())
            .filter(buildingModule -> gameData.getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty());
    }
}
