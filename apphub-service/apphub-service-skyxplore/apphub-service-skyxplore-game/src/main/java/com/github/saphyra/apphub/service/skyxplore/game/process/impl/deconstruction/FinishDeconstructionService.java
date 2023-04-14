package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

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
public class FinishDeconstructionService {
    public void finishDeconstruction(GameData gameData, UUID location, SyncCache syncCache, Deconstruction deconstruction) {
        log.info("Finishing deconstruction...");

        Building building = gameData.getBuildings()
            .findByBuildingId(deconstruction.getExternalReference());

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        gameData.getBuildings()
            .deleteByBuildingId(deconstruction.getExternalReference());

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        syncCache.deconstructionFinished(ownerId, location, deconstruction, building, surface);
    }
}
