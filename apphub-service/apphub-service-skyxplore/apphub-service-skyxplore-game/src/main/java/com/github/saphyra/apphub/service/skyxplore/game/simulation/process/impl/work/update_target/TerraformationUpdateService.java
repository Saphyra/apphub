package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
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
class TerraformationUpdateService {
    void updateTerraformation(SyncCache syncCache, GameData gameData, UUID location, UUID constructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to TERRAFORMATION {}", completedWorkPoints, constructionId);

        Construction terraformation = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(terraformation.getExternalReference());

        log.info("Before update: {}", terraformation);
        terraformation.setCurrentWorkPoints(terraformation.getCurrentWorkPoints() + completedWorkPoints);
        log.info("After update: {}", terraformation);


        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        syncCache.terraformationUpdated(ownerId, location, terraformation, surface);
    }
}
