package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
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
//TODO unit test
class ConstructBuildingModuleProcessHelper {
    private final ProductionOrderService productionOrderService;
    private final UseAllocatedResourceService useAllocatedResourceService;
    private final WorkProcessFactory workProcessFactory;
    private final AllocationRemovalService allocationRemovalService;

    void createProductionOrders(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID constructionId) {
        productionOrderService.createProductionOrdersForReservedStorages(progressDiff, gameData, processId, constructionId);
    }

    void startWork(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID constructionId) {
        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        useAllocatedResourceService.resolveAllocations(
            progressDiff,
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
        ).forEach(workProcess -> {
            gameData.getProcesses()
                .add(workProcess);
            progressDiff.save(workProcess.toModel());
        });
    }

    void finishConstruction(GameProgressDiff progressDiff, GameData gameData, UUID constructionId) {
        log.info("Finishing constructionArea construction...");
        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        allocationRemovalService.removeAllocationsAndReservations(progressDiff, gameData, constructionId);

        gameData.getConstructions()
            .remove(construction);

        progressDiff.delete(construction.getConstructionId(), GameItemType.CONSTRUCTION);
    }
}
