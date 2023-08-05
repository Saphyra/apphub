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
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction.ConstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction.ConstructionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.BuildingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructNewBuildingService {
    private final GameDao gameDao;
    private final AllBuildingService allBuildingService;
    private final BuildingFactory buildingFactory;
    private final ConstructionFactory constructionFactory;
    private final ResourceAllocationService resourceAllocationService;
    private final ConstructionProcessFactory constructionProcessFactory;
    private final SyncCacheFactory syncCacheFactory;

    public void constructNewBuilding(UUID userId, String dataId, UUID planetId, UUID surfaceId) {
        Optional<BuildingData> maybeBuildingData = allBuildingService.getOptional(dataId);
        if (maybeBuildingData.isEmpty()) {
            throw ExceptionFactory.invalidParam("dataId", "invalid value");
        }

        Game game = gameDao.findByUserIdValidated(userId);

        if (game.getData().getConstructions().findByExternalReference(surfaceId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Cannot build on surface under terraformation on planet " + planetId + " and surface " + surfaceId);
        }

        if (game.getData().getBuildings().findBySurfaceId(surfaceId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Building already exists on planet " + planetId + " and surface " + surfaceId);
        }

        BuildingData buildingData = maybeBuildingData.get();
        ConstructionRequirements constructionRequirements = buildingData.getConstructionRequirements()
            .get(1);

        Surface surface = game.getData()
            .getSurfaces()
            .findBySurfaceId(surfaceId);

        if (!buildingData.getPlaceableSurfaceTypes().contains(surface.getSurfaceType())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, dataId + " cannot be built to surfaceType " + surface.getSurfaceType());
        }

        Building building = buildingFactory.create(dataId, planetId, surfaceId, 0);
        Construction construction = constructionFactory.create(
            building.getBuildingId(),
            ConstructionType.CONSTRUCTION,
            planetId,
            constructionRequirements.getParallelWorkers(),
            constructionRequirements.getRequiredWorkPoints()
        );

        game.getData()
            .getConstructions()
            .add(construction);

        Planet planet = game.getData()
            .getPlanets()
            .get(planetId);

        SyncCache syncCache = syncCacheFactory.create(game);

        game.getEventLoop()
            .processWithWait(() -> {
                resourceAllocationService.processResourceRequirements(
                    syncCache,
                    game.getData(),
                    planetId,
                    planet.getOwner(),
                    construction.getConstructionId(),
                    constructionRequirements.getRequiredResources()
                );

                game.getData()
                    .getBuildings()
                    .add(building);

                ConstructionProcess constructionProcess = constructionProcessFactory.create(game.getData(), planetId, construction.getConstructionId());

                game.getData()
                    .getProcesses()
                    .add(constructionProcess);

                syncCache.constructionCreated(userId, planetId, construction, surface, constructionProcess);
            }, syncCache)
            .getOrThrow();
    }
}
