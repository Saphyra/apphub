package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDeconstructionService {
    private final SyncCacheFactory syncCacheFactory;
    private final GameDao gameDao;

    public void cancelDeconstructionOfDeconstruction(UUID userId, UUID planetId, UUID deconstructionId) {
        Game game = gameDao.findByUserIdValidated(userId);

        Planet planet = game.getData()
            .getPlanets()
            .get(planetId);

        Deconstruction deconstruction = game.getData()
            .getDeconstructions()
            .findByDeconstructionId(deconstructionId);

        Building building = game.getData()
            .getBuildings()
            .findByBuildingId(deconstruction.getExternalReference());

        processCancellation(game, planet, building, deconstruction);
    }

    public void cancelDeconstructionOfBuilding(UUID userId, UUID planetId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);

        Planet planet = game.getData()
            .getPlanets()
            .get(planetId);

        Building building = game.getData()
            .getBuildings()
            .findByBuildingId(buildingId);

        Deconstruction deconstruction = game.getData()
            .getDeconstructions()
            .findByExternalReferenceValidated(buildingId);

        processCancellation(game, planet, building, deconstruction);
    }

    private void processCancellation(Game game, Planet planet, Building building, Deconstruction deconstruction) {
        Surface surface = game.getData()
            .getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        SyncCache syncCache = syncCacheFactory.create(game);
        game.getEventLoop()
            .processWithWait(() -> {

                    game.getData()
                        .getProcesses()
                        .findByExternalReferenceAndTypeValidated(deconstruction.getDeconstructionId(), ProcessType.DECONSTRUCTION)
                        .cleanup(syncCache);

                    game.getData()
                        .getDeconstructions()
                        .remove(deconstruction);

                    syncCache.deconstructionCancelled(planet.getOwner(), planet.getPlanetId(), deconstruction.getDeconstructionId(), surface);
                },
                syncCache
            )
            .getOrThrow();
    }
}
