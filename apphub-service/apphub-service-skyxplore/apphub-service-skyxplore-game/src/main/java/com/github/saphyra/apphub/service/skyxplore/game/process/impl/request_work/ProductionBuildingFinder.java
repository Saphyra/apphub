package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
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
class ProductionBuildingFinder {
    private final BuildingCapacityCalculator buildingCapacityCalculator;

    Optional<UUID> findSuitableProductionBuilding(Planet planet, String buildingDataId) {
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
