package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Future;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO fix unit test
public class CancelConstructionService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final WsMessageSender messageSender;
    private final SleepService sleepService;
    private final SyncCacheFactory syncCacheFactory;

    public void cancelConstructionOfConstruction(UUID userId, UUID planetId, UUID constructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game.getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .values()
            .stream()
            .filter(s -> nonNull(s.getBuilding()))
            .filter(s -> nonNull(s.getBuilding().getConstruction()))
            .filter(s -> s.getBuilding().getConstruction().getConstructionId().equals(constructionId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Construction not found with id " + constructionId));
        Building building = surface
            .getBuilding();

        SurfaceResponse surfaceResponse = processCancellation(game, planet, surface, building);

        messageSender.planetSurfaceModified(userId, planet.getPlanetId(), surfaceResponse);
    }

    public SurfaceResponse cancelConstructionOfBuilding(UUID userId, UUID planetId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game.getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByBuildingIdValidated(buildingId);
        Building building = surface
            .getBuilding();

        return processCancellation(game, planet, surface, building);
    }

    @SneakyThrows
    private SurfaceResponse processCancellation(Game game, Planet planet, Surface surface, Building building) {
        Construction construction = building.getConstruction();
        if (isNull(construction)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Construction not found on planet " + planet.getPlanetId() + " and building " + building.getBuildingId());
        }

        SyncCache syncCache = syncCacheFactory.create();
        Future<ExecutionResult<SurfaceResponse>> future = game.getEventLoop()
            .processWithResponse(() -> {
                    game.getProcesses().findByExternalReferenceAndTypeValidated(construction.getConstructionId(), ProcessType.CONSTRUCTION)
                        .cancel(syncCache);
                    if (building.getLevel() == 0) {
                        surface.setBuilding(null);
                        gameDataProxy.deleteItem(building.getBuildingId(), GameItemType.BUILDING);
                    }

                    syncCache.addMessage(
                        planet.getOwner(),
                        WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
                        planet.getPlanetId(),
                        () -> messageSender.planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), construction.getConstructionId())
                    );

                    return surfaceToResponseConverter.convert(surface);
                },
                syncCache
            );

        while (!future.isDone()) {
            sleepService.sleep(100);
        }

        return future.get()
            .getOrThrow();
    }
}
