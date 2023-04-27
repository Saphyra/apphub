package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructionUpdateService {
    void updateDeconstruction(SyncCache syncCache, GameData gameData, UUID location, UUID deconstructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to DECONSTRUCTION {}", completedWorkPoints, deconstructionId);

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionId(deconstructionId);

        Building building = gameData.getBuildings()
            .findByBuildingId(deconstruction.getExternalReference());

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        log.info("Before update: {}", deconstruction);
        deconstruction.increaseWorkPoints(completedWorkPoints);
        log.info("After update: {}", deconstruction);

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        syncCache.deconstructionUpdated(ownerId, location, deconstruction, surface);
    }
}
