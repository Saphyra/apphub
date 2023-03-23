package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

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

    Optional<UUID> findSuitableProductionBuilding(GameData gameData, UUID planetId, String buildingDataId) {
        log.info("Searching for suitable {} on planet {}", buildingDataId, planetId);

        return gameData.getBuildings()
            .getByLocationAndDataId(planetId, buildingDataId)
            .stream()
            .filter(building -> buildingCapacityCalculator.calculateCapacity(gameData, building) > 0)
            .map(Building::getBuildingId)
            .findAny();
    }
}
