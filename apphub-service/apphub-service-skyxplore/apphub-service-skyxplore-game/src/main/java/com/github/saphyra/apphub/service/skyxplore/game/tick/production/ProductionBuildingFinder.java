package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProductionBuildingFinder {
    private final ProductionBuildingService productionBuildingService;
    private final BuildingAvailabilityCalculator buildingAvailabilityCalculator;

    Optional<Building> findProducer(Planet planet, String dataId) {
        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> nonNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .filter(building -> productionBuildingService.containsKey(building.getDataId()))
            .filter(building -> productionBuildingService.get(building.getDataId()).getGives().containsKey(dataId))
            .max(Comparator.comparing(building -> buildingAvailabilityCalculator.calculateBuildingAvailability(planet, building, dataId)));
    }
}
