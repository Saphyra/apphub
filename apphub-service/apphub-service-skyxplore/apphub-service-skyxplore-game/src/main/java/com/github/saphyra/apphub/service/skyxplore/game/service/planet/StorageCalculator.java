package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling.DwellingBuildingDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
public class StorageCalculator {
    private final DwellingBuildingDataService dwellingBuildingDataService;

    public int calculateDwellingCapacity(GameData gameData, UUID location) {
        return gameData.getBuildingModules()
            .getByLocation(location)
            .stream()
            .filter(buildingModule -> dwellingBuildingDataService.containsKey(buildingModule.getDataId()))
            .filter(buildingModule -> gameData.getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isEmpty())
            .mapToInt(buildingModule -> dwellingBuildingDataService.get(buildingModule.getDataId()).getCapacity())
            .sum();
    }
}
