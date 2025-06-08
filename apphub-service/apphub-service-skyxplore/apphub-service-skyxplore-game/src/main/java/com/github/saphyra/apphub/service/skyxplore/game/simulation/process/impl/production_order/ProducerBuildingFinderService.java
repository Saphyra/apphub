package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProducerBuildingModule;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.BuildingCapacityCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ProducerBuildingFinderService {
    private final ProductionBuildingModuleService productionBuildingModuleService;
    private final BuildingCapacityCalculator buildingCapacityCalculator;

    Optional<UUID> findProducerBuildingId(GameData gameData, UUID location, String resourceDataId) {
        return gameData.getBuildingModules()
            .getByLocation(location)
            .stream()
            .filter(buildingModule -> canProduce(resourceDataId, buildingModule))
            .filter(buildingModule -> buildingCapacityCalculator.isAvailable(gameData, buildingModule))
            .map(BuildingModule::getBuildingModuleId)
            .findAny();
    }

    private boolean canProduce(String resourceDataId, BuildingModule buildingModule) {
        String buildingDataId = buildingModule.getDataId();
        log.debug("Checking if {} can produce {}", buildingDataId, resourceDataId);
        ProducerBuildingModule producerBuildingModule = productionBuildingModuleService.get(buildingDataId);
        if (isNull(producerBuildingModule)) {
            return false;
        }
        return producerBuildingModule.getProduces()
            .stream()
            .anyMatch(production -> production.getResourceDataId().endsWith(resourceDataId));
    }
}
