package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.UseAllocatedResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order.ProductionOrderService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionProcessHelper {
    private final UseAllocatedResourceService useAllocatedResourceService;
    private final WorkProcessFactory workProcessFactory;
    private final AllocationRemovalService allocationRemovalService;
    private final ProductionOrderService productionOrderService;

    void startWork(SyncCache syncCache, GameData gameData, UUID processId, UUID constructionId) {
        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);
        Planet planet = gameData.getPlanets()
            .get(construction.getLocation());

        useAllocatedResourceService.resolveAllocations(
            syncCache,
            gameData,
            construction.getLocation(),
            planet.getOwner(),
            constructionId
        );

        workProcessFactory.createForConstruction(
            gameData,
            processId,
            constructionId,
            construction.getLocation(),
            construction.getRequiredWorkPoints()
        ).forEach(requestWorkProcess -> {
            gameData.getProcesses()
                .add(requestWorkProcess);
            syncCache.saveGameItem(requestWorkProcess.toModel());
        });
    }

    void finishConstruction(SyncCache syncCache, GameData gameData, UUID constructionId) {
        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        Planet planet = gameData.getPlanets()
            .get(construction.getLocation());

        Building building = gameData.getBuildings()
            .findByBuildingId(construction.getExternalReference());

        building.increaseLevel();
        log.info("Upgraded building: {}", building);

        gameData.getConstructions()
            .remove(construction);

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        allocationRemovalService.removeAllocationsAndReservations(syncCache, gameData, construction.getLocation(), planet.getOwner(), constructionId);
        syncCache.constructionFinished(planet.getOwner(), construction.getLocation(), construction, building, surface);
    }

    public void createProductionOrders(SyncCache syncCache, GameData gameData, UUID processId, UUID constructionId) {
        productionOrderService.createProductionOrdersForReservedStorages(syncCache, gameData, processId, constructionId);
    }
}
