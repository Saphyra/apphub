package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CancelConstructionService {
    private final GameDao gameDao;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final WsMessageSender messageSender;
    private final SyncCacheFactory syncCacheFactory;
    private final BuildingToModelConverter buildingToModelConverter;
    private final AllocationRemovalService allocationRemovalService;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    public void cancelConstructionOfConstruction(UUID userId, UUID planetId, UUID constructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Construction construction = gameData.getConstructions()
            .findByIdValidated(constructionId);

        Building building = gameData.getBuildings()
            .findByBuildingId(construction.getExternalReference());

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        Planet planet = gameData.getPlanets()
            .get(planetId);

        SurfaceResponse surfaceResponse = processCancellation(game, planet, surface, building);

        messageSender.planetSurfaceModified(userId, planet.getPlanetId(), surfaceResponse);
    }

    public SurfaceResponse cancelConstructionOfBuilding(UUID userId, UUID planetId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);

        Planet planet = game.getData()
            .getPlanets()
            .get(planetId);

        Building building = game.getData()
            .getBuildings()
            .findByBuildingId(buildingId);

        Surface surface = game.getData()
            .getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        return processCancellation(game, planet, surface, building);
    }

    @SneakyThrows
    private SurfaceResponse processCancellation(Game game, Planet planet, Surface surface, Building building) {
        GameData gameData = game.getData();

        Construction construction = gameData.getConstructions()
            .findByExternalReferenceValidated(building.getBuildingId());
        if (isNull(construction)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Construction not found on planet " + planet.getPlanetId() + " and building " + building.getBuildingId());
        }

        SyncCache syncCache = syncCacheFactory.create();
        return game.getEventLoop()
            .processWithResponseAndWait(() -> {
                    gameData.getProcesses()
                        .findByExternalReferenceAndTypeValidated(construction.getConstructionId(), ProcessType.CONSTRUCTION)
                        .cancel(syncCache);

                    gameData.getConstructions()
                        .deleteById(construction.getConstructionId());
                    syncCache.deleteGameItem(construction.getConstructionId(), GameItemType.CONSTRUCTION);
                    syncCache.saveGameItem(buildingToModelConverter.convert(game.getGameId(), building));

                    allocationRemovalService.removeAllocationsAndReservations(syncCache, gameData, planet.getPlanetId(), planet.getOwner(), construction.getConstructionId());

                    if (building.getLevel() == 0) {
                        gameData.getBuildings()
                            .deleteByBuildingId(building.getBuildingId());
                        syncCache.deleteGameItem(building.getBuildingId(), GameItemType.BUILDING);
                    }

                    syncCache.addMessage(
                        planet.getOwner(),
                        WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
                        planet.getPlanetId(),
                        () -> messageSender.planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), construction.getConstructionId())
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
