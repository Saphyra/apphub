package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.AllBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction.ConstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction.ConstructionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpgradeBuildingService {
    private final GameDao gameDao;
    private final AllBuildingService allBuildingService;
    private final ConstructionFactory constructionFactory;
    private final ResourceAllocationService resourceAllocationService;
    private final ConstructionProcessFactory constructionProcessFactory;
    private final SyncCacheFactory syncCacheFactory;

    public void upgradeBuilding(UUID userId, UUID planetId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);

        if (game.getData().getDeconstructions().findByExternalReference(buildingId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, buildingId + " on planet " + planetId + " is under deconstruction.");
        }

        if (game.getData().getConstructions().findByExternalReference(buildingId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Construction already in progress on planet " + planetId + " and building " + buildingId);
        }

        Building building = game.getData()
            .getBuildings()
            .findByBuildingId(buildingId);

        BuildingData buildingData = allBuildingService.get(building.getDataId());
        ConstructionRequirements constructionRequirements = buildingData.getConstructionRequirements()
            .get(building.getLevel() + 1);
        if (isNull(constructionRequirements)) {
            throw ExceptionFactory.notLoggedException(
                HttpStatus.FORBIDDEN,
                ErrorCode.FORBIDDEN_OPERATION,
                "Max level " + building.getLevel() + " reached for dataId " + building.getDataId() + " on planet " + planetId + " and building " + building
            );
        }

        Construction construction = constructionFactory.create(
            building.getBuildingId(),
            ConstructionType.CONSTRUCTION,
            planetId,
            constructionRequirements.getParallelWorkers(),
            constructionRequirements.getRequiredWorkPoints()
        );

        Surface surface = game.getData()
            .getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        UUID ownerId = game.getData()
            .getPlanets()
            .get(planetId)
            .getOwner();

        SyncCache syncCache = syncCacheFactory.create(game);

        game.getEventLoop()
            .processWithWait(() -> {
                resourceAllocationService.processResourceRequirements(
                    syncCache,
                    game.getData(),
                    planetId,
                    ownerId,
                    construction.getConstructionId(),
                    constructionRequirements.getRequiredResources()
                );

                game.getData()
                    .getConstructions()
                    .add(construction);

                ConstructionProcess constructionProcess = constructionProcessFactory.create(game.getData(), planetId, construction.getConstructionId());

                game.getData()
                    .getProcesses()
                    .add(constructionProcess);

                syncCache.constructionCreated(userId, planetId, construction, surface, constructionProcess);
            }, syncCache)
            .getOrThrow();
    }
}
