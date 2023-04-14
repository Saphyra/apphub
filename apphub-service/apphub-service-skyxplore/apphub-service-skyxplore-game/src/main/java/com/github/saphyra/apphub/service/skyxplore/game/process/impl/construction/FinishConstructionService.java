package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
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
class FinishConstructionService {
    private final AllocationRemovalService allocationRemovalService;

    void finishConstruction(SyncCache syncCache, GameData gameData, UUID location, Building building, Construction construction) {
        log.info("Finishing construction...");

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        allocationRemovalService.removeAllocationsAndReservations(syncCache, gameData, location, ownerId, construction.getConstructionId());

        building.increaseLevel();

        gameData.getConstructions()
            .deleteByConstructionId(construction.getConstructionId());

        log.info("Upgraded building: {}", building);

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        syncCache.constructionFinished(ownerId, location, construction, building, surface);
    }
}
