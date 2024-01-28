package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.BuildingConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.UseAllocatedResourceService;
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
    private final BuildingConverter buildingConverter;

    void startWork(GameProgressDiff gameProgressDiff, GameData gameData, UUID processId, UUID constructionId) {
        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        useAllocatedResourceService.resolveAllocations(
            gameProgressDiff,
            gameData,
            construction.getLocation(),
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
            gameProgressDiff.save(requestWorkProcess.toModel());
        });
    }

    void finishConstruction(GameProgressDiff gameProgressDiff, GameData gameData, UUID constructionId) {
        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        Building building = gameData.getBuildings()
            .findByBuildingId(construction.getExternalReference());

        building.increaseLevel();
        log.info("Upgraded building: {}", building);

        gameData.getConstructions()
            .remove(construction);

        allocationRemovalService.removeAllocationsAndReservations(gameProgressDiff, gameData, constructionId);

        gameProgressDiff.delete(constructionId, GameItemType.CONSTRUCTION);
        gameProgressDiff.save(buildingConverter.toModel(gameData.getGameId(), building));
    }

    public void createProductionOrders(GameProgressDiff gameProgressDiff, GameData gameData, UUID processId, UUID constructionId) {
        productionOrderService.createProductionOrdersForReservedStorages(gameProgressDiff, gameData, processId, constructionId);
    }
}
