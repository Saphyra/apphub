package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProductionBuildingFinder {
    private final ProductionBuildingService productionBuildingService;

    Optional<UUID> findSuitableProductionBuildings(Planet planet, String buildingDataId) {
        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .filter(building -> building.getDataId().equals(buildingDataId))
            .filter(building -> planet.getBuildingAllocations().containsKey(building.getBuildingId()))
            .filter(building -> calculateCapacity(planet, building) > 0)
            .map(Building::getBuildingId)
            .findAny();
    }

    private int calculateCapacity(Planet planet, Building building) {
        int maxWorkers = building.getLevel() * productionBuildingService.get(building.getDataId()).getWorkers();

        int assignedWorkplaces = planet.getBuildingAllocations()
            .get(building.getBuildingId())
            .size();

        return maxWorkers - assignedWorkplaces;
    }
}
