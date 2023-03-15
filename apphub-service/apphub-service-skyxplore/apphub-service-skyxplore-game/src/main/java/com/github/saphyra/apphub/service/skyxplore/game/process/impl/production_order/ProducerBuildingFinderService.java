package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.BuildingCapacityCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ProducerBuildingFinderService {
    private final ProductionBuildingService productionBuildingService;
    private final BuildingCapacityCalculator buildingCapacityCalculator;

    Optional<String> findProducerBuildingDataId(Planet planet, String dataId) {
        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .filter(surface -> productionBuildingService.containsKey(surface.getBuilding().getDataId()))
            .filter(surface -> canProduce(dataId, surface))
            .map(Surface::getBuilding)
            .filter(building -> buildingCapacityCalculator.calculateCapacity(planet, building) > 0)
            .map(Building::getDataId)
            .findAny();
    }

    private boolean canProduce(String dataId, Surface surface) {
        Map<String, ProductionData> gives = productionBuildingService.get(surface.getBuilding().getDataId()).getGives();
        if (gives.containsKey(dataId)) {
            return gives.get(dataId)
                .getPlaced()
                .contains(surface.getSurfaceType());
        }
        return false;
    }
}
