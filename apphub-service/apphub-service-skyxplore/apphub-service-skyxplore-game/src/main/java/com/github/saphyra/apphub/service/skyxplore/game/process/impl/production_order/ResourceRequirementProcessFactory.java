package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ResourceRequirementProcessFactory {
    private final ProductionBuildingService productionBuildingService;
    private final ProductionRequirementsAllocationService productionRequirementsAllocationService;
    private final ProductionOrderProcessFactory productionOrderProcessFactory;

    List<ProductionOrderProcess> createResourceRequirementProcesses(SyncCache syncCache, UUID processId, Game game, Planet planet, String dataId, int amount, String producerBuildingDataId) {
        return productionBuildingService.get(producerBuildingDataId)
            .getGives()
            .get(dataId)
            .getConstructionRequirements()
            .getRequiredResources()
            .entrySet()
            .stream()
            .map(entry -> new BiWrapper<>(entry.getKey(), entry.getValue() * amount))
            .peek(biWrapper -> log.info("Required {} of {}", biWrapper.getEntity2(), biWrapper.getEntity1()))
            .map(entry -> productionRequirementsAllocationService.allocate(syncCache, game.getGameId(), planet, processId, entry.getEntity1(), entry.getEntity2()))
            .flatMap(reservedStorageId -> productionOrderProcessFactory.create(processId, game, planet, reservedStorageId).stream())
            .collect(Collectors.toList());
    }
}
