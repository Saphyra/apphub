package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.util.HeadquartersUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingCapacityCalculator {
    private final ProductionBuildingService productionBuildingService;
    private final HeadquartersUtil headquartersUtil;

    /**
     * result = number of workplaces * level - allocated workplaces
     */
    public int calculateCapacity(GameData gameData, Building building) {
        if (gameData.getDeconstructions().findByExternalReference(building.getBuildingId()).isPresent()) {
            log.info("Building is being deconstructed");
            return 0;
        }
        Integer workers = Optional.ofNullable(productionBuildingService.get(building.getDataId()))
            .map(ProductionBuildingData::getWorkers)
            .orElseGet(headquartersUtil::getWorkers);
        int maxWorkers = building.getLevel() * workers;

        int assignedWorkplaces = gameData.getBuildingAllocations()
            .getByBuildingId(building.getBuildingId())
            .size();

        int result = maxWorkers - assignedWorkplaces;
        log.info("Workplaces on level {} {}: {}. Already assigned: {}. Remaining: {}", building.getLevel(), building.getDataId(), maxWorkers, assignedWorkplaces, result);
        return result;
    }
}
