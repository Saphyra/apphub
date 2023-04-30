package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelTerraformationService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;
    private final SyncCacheFactory syncCacheFactory;
    private final AllocationRemovalService allocationRemovalService;

    public void cancelTerraformationQueueItem(UUID userId, UUID planetId, UUID constructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Construction terraformation = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(terraformation.getExternalReference());

        Planet planet = gameData.getPlanets()
            .get(planetId);

        processCancellation(game, planet, surface, terraformation);
    }

    void cancelTerraformationOfSurface(UUID userId, UUID planetId, UUID surfaceId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(surfaceId);

        Construction terraformation = gameData.getConstructions()
            .findByExternalReferenceValidated(surfaceId);

        Planet planet = gameData.getPlanets()
            .get(planetId);

        processCancellation(game, planet, surface, terraformation);
    }

    @SneakyThrows
    private void processCancellation(Game game, Planet planet, Surface surface, Construction terraformation) {
        SyncCache syncCache = syncCacheFactory.create(game);
        game.getEventLoop()
            .processWithWait(() -> {
                    game.getData()
                        .getProcesses()
                        .findByExternalReferenceAndTypeValidated(terraformation.getConstructionId(), ProcessType.TERRAFORMATION)
                        .cleanup(syncCache);

                    game.getData()
                        .getConstructions()
                        .remove(terraformation);

                    gameDataProxy.deleteItem(terraformation.getConstructionId(), GameItemType.CONSTRUCTION);

                    allocationRemovalService.removeAllocationsAndReservations(syncCache, game.getData(), planet.getPlanetId(), planet.getOwner(), terraformation.getConstructionId());

                    syncCache.terraformationCancelled(planet.getOwner(), planet.getPlanetId(), terraformation.getConstructionId(), surface);
                },
                syncCache
            )
            .getOrThrow();
    }
}
