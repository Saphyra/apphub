package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class BuildingAvailabilityCalculator {
    private final ProductionBuildingService productionBuildingService;

    double calculateBuildingAvailability(Planet planet, Building building, String dataId) {
        ProductionBuilding buildingData = productionBuildingService.get(building.getDataId());
        log.debug("{} found for {}", buildingData, building);

        Integer workPointsPerResource = buildingData.getGives()
            .get(dataId)
            .getConstructionRequirements()
            .getRequiredWorkPoints();
        double availabilityPerTick = buildingData.getWorkers() * building.getLevel() / (double) workPointsPerResource;

        int queuedAvailability = 1 + planet.getOrders()
            .stream()
            .filter(productionOrder -> building.getBuildingId().equals(productionOrder.getAssignee()))
            .mapToInt(productionOrder -> productionOrder.getRequiredWorkPoints() - productionOrder.getCurrentWorkPoints())
            .sum();

        double result = availabilityPerTick / queuedAvailability;
        log.debug("availabilityPerTick: {}, queuedAvailability: {}, buildingAvailability: {}", availabilityPerTick, queuedAvailability, result);
        return result;
    }
}
