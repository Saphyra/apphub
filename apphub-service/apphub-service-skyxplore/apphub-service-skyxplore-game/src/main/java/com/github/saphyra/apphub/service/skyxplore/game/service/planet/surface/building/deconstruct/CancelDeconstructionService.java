package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
        Planet planet = game.getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .values()
            .stream()
            .filter(s -> nonNull(s.getBuilding()))
            .filter(s -> nonNull(s.getBuilding().getDeconstruction()))
            .filter(s -> s.getBuilding().getDeconstruction().getDeconstructionId().equals(deconstructionId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Deconstruction not found with id " + deconstructionId));
        Building building = surface.getBuilding();

        SurfaceResponse surfaceResponse = processCancellation(game, planet, surface, building);

        messageSender.planetSurfaceModified(userId, planet.getPlanetId(), surfaceResponse);
    }

    public SurfaceResponse cancelDeconstructionOfBuilding(UUID userId, UUID planetId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game.getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByBuildingIdValidated(buildingId);
        Building building = surface.getBuilding();

        return processCancellation(game, planet, surface, building);
    }

    private SurfaceResponse processCancellation(Game game, Planet planet, Surface surface, Building building) {
        Deconstruction deconstruction = building.getDeconstruction();
        if (isNull(deconstruction)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Deconstruction not found on planet " + planet.getPlanetId() + " and building " + building.getBuildingId());
        }

        SyncCache syncCache = syncCacheFactory.create();
        return game.getEventLoop()
            .processWithResponseAndWait(() -> {
                    game.getProcesses()
                        .findByExternalReferenceAndTypeValidated(deconstruction.getDeconstructionId(), ProcessType.DECONSTRUCTION)
                        .cancel(syncCache);

                    building.setDeconstruction(null);
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
                            planetBuildingOverviewQueryService.getBuildingOverview(planet)
                        )
                    );

                    return surfaceToResponseConverter.convert(surface);
                },
                syncCache
            )
            .getOrThrow();
    }
}
