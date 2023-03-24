package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
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
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction.ConstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction.ConstructionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.BuildingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction.BuildingConstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
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
    private final GameDataProxy gameDataProxy;
    private final BuildingToModelConverter buildingToModelConverter;
    private final ConstructionToModelConverter constructionToModelConverter;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final BuildingConstructionToQueueItemConverter buildingConstructionToQueueItemConverter;
    private final QueueItemToResponseConverter queueItemToResponseConverter;
    private final WsMessageSender messageSender;
    private final ConstructionProcessFactory constructionProcessFactory;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    public SurfaceResponse constructNewBuilding(UUID userId, String dataId, UUID planetId, UUID surfaceId) {
        Optional<BuildingData> maybeBuildingData = allBuildingService.getOptional(dataId);
        if (maybeBuildingData.isEmpty()) {
            throw ExceptionFactory.invalidParam("dataId", "invalid value");
        }

        Game game = gameDao.findByUserIdValidated(userId);

        Planet planet = game.getData()
            .getPlanets()
            .get(planetId);

        Surface surface = game.getData()
            .getSurfaces()
            .findBySurfaceId(surfaceId);

        if (game.getData().getConstructions().findByExternalReference(surfaceId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Cannot build on surface under terraformation on planet " + planet + " and surface " + surfaceId);
        }

        if (game.getData().getBuildings().findBySurfaceId(surfaceId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Building already exists on planet " + planetId + " and surface " + surfaceId);
        }

        BuildingData buildingData = maybeBuildingData.get();
        ConstructionRequirements constructionRequirements = buildingData.getConstructionRequirements()
            .get(1);

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

        return game.getEventLoop()
            .processWithResponseAndWait(() -> {
                resourceAllocationService.processResourceRequirements(game.getData(), planetId, planet.getOwner(), construction.getConstructionId(), constructionRequirements.getRequiredResources());

                game.getData()
                    .getBuildings()
                    .add(building);

                QueueResponse queueResponse = queueItemToResponseConverter.convert(buildingConstructionToQueueItemConverter.convert(game.getData(), construction), game.getData(), planetId);
                messageSender.planetQueueItemModified(userId, planetId, queueResponse);
                messageSender.planetBuildingDetailsModified(userId, planetId, planetBuildingOverviewQueryService.getBuildingOverview(game.getData(), planetId));

                ConstructionProcess constructionProcess = constructionProcessFactory.create(game.getData(), planetId, building, construction);

                game.getData()
                    .getProcesses()
                    .add(constructionProcess);

                gameDataProxy.saveItem(
                    buildingToModelConverter.convert(game.getGameId(), building),
                    constructionToModelConverter.convert(game.getGameId(), construction),
                    constructionProcess.toModel()
                );

                return surfaceToResponseConverter.convert(game.getData(), surface);
            })
            .getOrThrow();
    }
}
