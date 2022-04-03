package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.BuildingCapacityCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProducerBuildingFinderService {
    private final ProductionBuildingService productionBuildingService;
    private final BuildingCapacityCalculator buildingCapacityCalculator;

    Optional<String> findProducerBuildingDataId(Planet planet, String dataId) {
        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .filter(building -> productionBuildingService.containsKey(building.getDataId()))
            .filter(building -> productionBuildingService.get(building.getDataId()).getGives().containsKey(dataId))
            .filter(building -> buildingCapacityCalculator.calculateCapacity(planet, building) > 0)
            .map(Building::getDataId)
            .findAny();
    }
}
