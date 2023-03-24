package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.BuildingCapacityCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ProducerBuildingFinderService {
    private final ProductionBuildingService productionBuildingService;
    private final BuildingCapacityCalculator buildingCapacityCalculator;

    Optional<String> findProducerBuildingDataId(GameData gameData, UUID location, String dataId) {
        return gameData.getBuildings()
            .getByLocation(location)
            .stream()
            .filter(building -> canProduce(gameData, dataId, building))
            .filter(building -> buildingCapacityCalculator.calculateCapacity(gameData, building) > 0)
            .map(Building::getDataId)
            .findAny();
    }

    private boolean canProduce(GameData gameData, String dataId, Building building) {
        Map<String, ProductionData> gives = productionBuildingService.get(building.getDataId()).getGives();
        if (gives.containsKey(dataId)) {
            return gives.get(dataId)
                .getPlaced()
                .contains(gameData.getSurfaces().findBySurfaceId(building.getSurfaceId()).getSurfaceType());
        }
        return false;
    }
}
