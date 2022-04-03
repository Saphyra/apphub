package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.BuildingCapacityCalculator;
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
    private final BuildingCapacityCalculator buildingCapacityCalculator;

    Optional<UUID> findSuitableProductionBuildings(Planet planet, String buildingDataId) {
        log.info("Searching for suitable {} on planet {}", buildingDataId, planet.getPlanetId());
        log.info("Actual BuildingAllocations: {}", planet.getBuildingAllocations());

        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .filter(building -> building.getDataId().equals(buildingDataId))
            .filter(building -> buildingCapacityCalculator.calculateCapacity(planet, building) > 0)
            .map(Building::getBuildingId)
            .findAny();
    }
}
