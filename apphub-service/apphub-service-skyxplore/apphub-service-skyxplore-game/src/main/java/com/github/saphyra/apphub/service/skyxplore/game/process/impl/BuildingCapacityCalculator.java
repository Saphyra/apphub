package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingCapacityCalculator {
    private final ProductionBuildingService productionBuildingService;

    /**
     * result = number of workplaces * level - allocated workplaces
     */
    public int calculateCapacity(GameData gameData, Building building) {
        Integer workers = productionBuildingService.get(building.getDataId())
            .getWorkers();
        int maxWorkers = building.getLevel() * workers;

        int assignedWorkplaces = gameData.getBuildingAllocations()
            .getByBuildingId(building.getBuildingId())
            .size();

        int result = maxWorkers - assignedWorkplaces;
        log.info("Workplaces on level {} {}: {}. Already assigned: {}. Remaining: {}", building.getLevel(), building.getDataId(), maxWorkers, assignedWorkplaces, result);
        return result;
    }
}
