package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingCapacityCalculator {
    private final ProductionBuildingService productionBuildingService;

    public int calculateCapacity(Planet planet, Building building) {
        int maxWorkers = building.getLevel() * productionBuildingService.get(building.getDataId())
            .getWorkers();

        int assignedWorkplaces = planet.getBuildingAllocations()
            .get(building.getBuildingId())
            .size();

        int result = maxWorkers - assignedWorkplaces;
        log.info("Workplaces on level {} {}: {}. Already assigned: {}. Remaining: {}", building.getLevel(), building.getDataId(), maxWorkers, assignedWorkplaces, result);
        return result;
    }
}
