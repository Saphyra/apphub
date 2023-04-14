package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
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
    private final SyncCacheFactory syncCacheFactory;
    private final AllocationRemovalService allocationRemovalService;

    public void cancelConstructionOfConstruction(UUID userId, UUID planetId, UUID constructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        Building building = gameData.getBuildings()
            .findByBuildingId(construction.getExternalReference());

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        Planet planet = gameData.getPlanets()
            .get(planetId);

        processCancellation(game, planet, surface, building);
    }

    public void cancelConstructionOfBuilding(UUID userId, UUID planetId, UUID buildingId) {
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

        processCancellation(game, planet, surface, building);
    }

    @SneakyThrows
    private void processCancellation(Game game, Planet planet, Surface surface, Building building) {
        GameData gameData = game.getData();

        Construction construction = gameData.getConstructions()
            .findByExternalReferenceValidated(building.getBuildingId());
        if (isNull(construction)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Construction not found on planet " + planet.getPlanetId() + " and building " + building.getBuildingId());
        }

        SyncCache syncCache = syncCacheFactory.create(game);
        game.getEventLoop()
            .processWithWait(() -> {
                    gameData.getProcesses()
                        .findByExternalReferenceAndTypeValidated(construction.getConstructionId(), ProcessType.CONSTRUCTION)
                        .cancel(syncCache);

                    gameData.getConstructions()
                        .deleteByConstructionId(construction.getConstructionId());

                    allocationRemovalService.removeAllocationsAndReservations(syncCache, gameData, planet.getPlanetId(), planet.getOwner(), construction.getConstructionId());

                    syncCache.constructionCancelled(planet.getOwner(), planet.getPlanetId(), construction.getConstructionId(), surface);

                    if (building.getLevel() == 0) {
                        gameData.getBuildings()
                            .deleteByBuildingId(building.getBuildingId());
                        syncCache.deleteGameItem(building.getBuildingId(), GameItemType.BUILDING);
                    }
                },
                syncCache
            )
            .getOrThrow();
    }
}
