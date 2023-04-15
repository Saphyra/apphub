package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.BuildingCapacityCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionBuildingFinder {
    private final BuildingCapacityCalculator buildingCapacityCalculator;

    Optional<UUID> findSuitableProductionBuilding(GameData gameData, UUID location, String buildingDataId) {
        log.info("Searching for suitable {} at {}", buildingDataId, location);

        return gameData.getBuildings()
            .getByLocationAndDataId(location, buildingDataId)
            .stream()
            .filter(building -> buildingCapacityCalculator.calculateCapacity(gameData, building) > 0)
            .map(Building::getBuildingId)
            .findAny();
    }
}
