package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingCapacityCalculator {
    public boolean isAvailable(GameData gameData, BuildingModule buildingModule) {
        if (gameData.getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isPresent()) {
            log.info("Building {} is being deconstructed", buildingModule.getBuildingModuleId());
            return false;
        }

        if (gameData.getConstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isPresent()) {
            log.info("Building {} is being constructed", buildingModule.getBuildingModuleId());
            return false;
        }

        return gameData.getBuildingModuleAllocations()
            .getByBuildingModuleId(buildingModule.getBuildingModuleId())
            .isEmpty();
    }
}
