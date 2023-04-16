package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.UseAllocatedResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class TerraformationProcessHelper {
    private final UseAllocatedResourceService useAllocatedResourceService;
    private final WorkProcessFactory workProcessFactory;
    private final AllocationRemovalService allocationRemovalService;

    void startWork(SyncCache syncCache, GameData gameData, UUID processId, UUID terraformationId) {
        Construction construction = gameData.getConstructions().findByConstructionIdValidated(terraformationId);
        Planet planet = gameData.getPlanets()
            .get(construction.getLocation());

        useAllocatedResourceService.resolveAllocations(
            syncCache,
            gameData,
            construction.getLocation(),
            planet.getOwner(),
            terraformationId
        );

        workProcessFactory.createForTerraformation(
            gameData,
            processId,
            construction.getConstructionId(),
            construction.getLocation(),
            construction.getRequiredWorkPoints()
        ).forEach(requestWorkProcess -> {
            gameData.getProcesses()
                .add(requestWorkProcess);
            syncCache.saveGameItem(requestWorkProcess.toModel());
        });
    }

    void finishTerraformation(SyncCache syncCache, GameData gameData, UUID terraformationId) {
        log.info("Finishing terraformation...");
        Construction terraformation = gameData.getConstructions()
            .findByConstructionIdValidated(terraformationId);

        Planet planet = gameData.getPlanets()
            .get(terraformation.getLocation());

        allocationRemovalService.removeAllocationsAndReservations(syncCache, gameData, planet.getPlanetId(), planet.getOwner(), terraformationId);

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(terraformation.getExternalReference());

        surface.setSurfaceType(SurfaceType.valueOf(terraformation.getData()));

        log.info("Terraformed surface: {}", surface);

        gameData.getConstructions()
            .remove(terraformation);

        syncCache.terraformationFinished(planet.getOwner(), planet.getPlanetId(), terraformation, surface);
    }
}
