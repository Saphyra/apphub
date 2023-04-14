package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FinishTerraformationService {
    private final AllocationRemovalService allocationRemovalService;

    void finishTerraformation(SyncCache syncCache, GameData gameData, UUID location, Construction terraformation) {
        log.info("Finishing terraformation...");

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        allocationRemovalService.removeAllocationsAndReservations(syncCache, gameData, location, ownerId, terraformation.getConstructionId());

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(terraformation.getExternalReference());

        surface.setSurfaceType(SurfaceType.valueOf(terraformation.getData()));

        gameData.getConstructions()
            .deleteByConstructionId(terraformation.getConstructionId());

        log.info("Terraformed surface: {}", surface);

        syncCache.terraformationFinished(ownerId, location, terraformation, surface);
    }
}
