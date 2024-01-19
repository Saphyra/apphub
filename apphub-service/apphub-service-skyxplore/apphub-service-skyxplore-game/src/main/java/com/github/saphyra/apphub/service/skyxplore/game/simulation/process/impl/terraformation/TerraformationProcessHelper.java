package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.SurfaceConverter;
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
class TerraformationProcessHelper {
    private final UseAllocatedResourceService useAllocatedResourceService;
    private final WorkProcessFactory workProcessFactory;
    private final AllocationRemovalService allocationRemovalService;
    private final ProductionOrderService productionOrderService;
    private final SurfaceConverter surfaceConverter;

    void startWork(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID terraformationId) {
        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(terraformationId);

        useAllocatedResourceService.resolveAllocations(
            progressDiff,
            gameData,
            construction.getLocation(),
            terraformationId
        );

        workProcessFactory.createForTerraformation(
            gameData,
            processId,
            terraformationId,
            construction.getLocation(),
            construction.getRequiredWorkPoints()
        ).forEach(workProcess -> {
            gameData.getProcesses()
                .add(workProcess);
            progressDiff.save(workProcess.toModel());
        });
    }

    void finishTerraformation(GameProgressDiff progressDiff, GameData gameData, UUID terraformationId) {
        log.info("Finishing terraformation...");
        Construction terraformation = gameData.getConstructions()
            .findByConstructionIdValidated(terraformationId);

        allocationRemovalService.removeAllocationsAndReservations(progressDiff, gameData, terraformationId);

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(terraformation.getExternalReference());

        surface.setSurfaceType(SurfaceType.valueOf(terraformation.getData()));

        log.info("Terraformed surface: {}", surface);

        gameData.getConstructions()
            .remove(terraformation);

        progressDiff.save(surfaceConverter.toModel(gameData.getGameId(), surface));
        progressDiff.delete(terraformation.getConstructionId(), GameItemType.CONSTRUCTION);
    }

    public void createProductionOrders(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID terraformationId) {
        productionOrderService.createProductionOrdersForReservedStorages(progressDiff, gameData, processId, terraformationId);
    }
}
