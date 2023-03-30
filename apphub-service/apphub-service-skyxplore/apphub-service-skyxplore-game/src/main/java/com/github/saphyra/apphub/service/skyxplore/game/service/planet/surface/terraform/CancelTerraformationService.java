package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
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
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;
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

        SurfaceResponse surfaceResponse = processCancellation(game, planet, surface, terraformation);

        messageSender.planetSurfaceModified(userId, planetId, surfaceResponse);
    }

    SurfaceResponse cancelTerraformationOfSurface(UUID userId, UUID planetId, UUID surfaceId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(surfaceId);

        Construction terraformation = gameData.getConstructions()
            .findByExternalReferenceValidated(surfaceId);

        Planet planet = gameData.getPlanets()
            .get(planetId);

        return processCancellation(game, planet, surface, terraformation);
    }

    @SneakyThrows
    private SurfaceResponse processCancellation(Game game, Planet planet, Surface surface, Construction terraformation) {
        SyncCache syncCache = syncCacheFactory.create();
        return game.getEventLoop()
            .processWithResponseAndWait(() -> {
                    game.getData()
                        .getProcesses()
                        .findByExternalReferenceAndTypeValidated(terraformation.getConstructionId(), ProcessType.TERRAFORMATION)
                        .cancel(syncCache);

                    game.getData()
                        .getConstructions()
                        .deleteByConstructionId(terraformation.getConstructionId());

                    gameDataProxy.deleteItem(terraformation.getConstructionId(), GameItemType.CONSTRUCTION);

                    allocationRemovalService.removeAllocationsAndReservations(syncCache, game.getData(), planet.getPlanetId(), planet.getOwner(), terraformation.getConstructionId());

                    syncCache.addMessage(
                        planet.getOwner(),
                        WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
                        planet.getPlanetId(),
                        () -> messageSender.planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), terraformation.getConstructionId())
                    );

                    syncCache.addMessage(
                        planet.getOwner(),
                        WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED,
                        planet.getPlanetId(),
                        () -> messageSender.planetBuildingDetailsModified(
                            planet.getOwner(),
                            planet.getPlanetId(),
                            planetBuildingOverviewQueryService.getBuildingOverview(game.getData(), planet.getPlanetId())
                        )
                    );

                    return surfaceToResponseConverter.convert(game.getData(), surface);
                },
                syncCache
            )
            .getOrThrow();
    }
}
