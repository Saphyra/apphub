package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDeconstructionService {
    private final SyncCacheFactory syncCacheFactory;
    private final WsMessageSender messageSender;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
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

        SurfaceResponse surfaceResponse = processCancellation(game, planet, building, deconstruction);

        messageSender.planetSurfaceModified(userId, planet.getPlanetId(), surfaceResponse);
    }

    public SurfaceResponse cancelDeconstructionOfBuilding(UUID userId, UUID planetId, UUID buildingId) {
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

        return processCancellation(game, planet, building, deconstruction);
    }

    private SurfaceResponse processCancellation(Game game, Planet planet, Building building, Deconstruction deconstruction) {
        Surface surface = game.getData()
            .getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        SyncCache syncCache = syncCacheFactory.create();
        return game.getEventLoop()
            .processWithResponseAndWait(() -> {

                    game.getData()
                        .getProcesses()
                        .findByExternalReferenceAndTypeValidated(deconstruction.getDeconstructionId(), ProcessType.DECONSTRUCTION)
                        .cancel(syncCache);

                    game.getData()
                        .getDeconstructions()
                        .remove(deconstruction);

                    syncCache.deleteGameItem(deconstruction.getDeconstructionId(), GameItemType.DECONSTRUCTION);

                    syncCache.addMessage(
                        planet.getOwner(),
                        WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
                        planet.getPlanetId(),
                        () -> messageSender.planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), deconstruction.getDeconstructionId())
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
