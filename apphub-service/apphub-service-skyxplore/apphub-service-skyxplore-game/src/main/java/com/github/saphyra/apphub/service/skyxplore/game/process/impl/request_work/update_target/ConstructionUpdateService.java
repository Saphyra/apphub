package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionUpdateService {
    void updateConstruction(SyncCache syncCache, GameData gameData, UUID location, UUID constructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to CONSTRUCTION {}", completedWorkPoints, constructionId);

        Construction construction = gameData
            .getConstructions()
            .findByConstructionIdValidated(constructionId);

        Building building = gameData.getBuildings()
            .findByBuildingId(construction.getExternalReference());

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        log.info("Before update: {}", construction);
        construction.setCurrentWorkPoints(construction.getCurrentWorkPoints() + completedWorkPoints);
        log.info("After update: {}", construction);

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        syncCache.constructionUpdated(ownerId, location, construction, surface);
    }
}
