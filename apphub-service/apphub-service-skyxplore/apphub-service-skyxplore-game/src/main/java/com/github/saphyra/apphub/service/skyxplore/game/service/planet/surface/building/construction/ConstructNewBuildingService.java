package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.AllBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
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

import static java.util.Objects.nonNull;

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
        Planet planet = game
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByIdValidated(surfaceId);

        if (nonNull(surface.getTerraformation())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Cannot build on surface under terraformation on planet " + planet + " and surface " + surfaceId);
        }

        if (nonNull(surface.getBuilding())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Building already exists on planet " + planetId + " and surface " + surfaceId);
        }

        BuildingData buildingData = maybeBuildingData.get();
        ConstructionRequirements constructionRequirements = buildingData.getConstructionRequirements()
            .get(1);

        if (!buildingData.getPlaceableSurfaceTypes().contains(surface.getSurfaceType())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, dataId + " cannot be built to surfaceType " + surface.getSurfaceType());
        }

        Building building = buildingFactory.create(dataId, surfaceId, 0);
        Construction construction = constructionFactory.create(building.getBuildingId(), constructionRequirements.getParallelWorkers(), constructionRequirements.getRequiredWorkPoints());
        building.setConstruction(construction);

        resourceAllocationService.processResourceRequirements(game.getGameId(), planet, LocationType.PLANET, construction.getConstructionId(), constructionRequirements.getRequiredResources());

        surface.setBuilding(building);

        QueueResponse queueResponse = queueItemToResponseConverter.convert(buildingConstructionToQueueItemConverter.convert(building), planet);
        messageSender.planetQueueItemModified(userId, planetId, queueResponse);
        messageSender.planetBuildingDetailsModified(userId, planetId, planetBuildingOverviewQueryService.getBuildingOverview(planet));

        ConstructionProcess constructionProcess = constructionProcessFactory.create(game, planet, building);

        Processes processes = game.getProcesses();
        synchronized (processes) {
            processes.add(constructionProcess);
        }

        gameDataProxy.saveItem(
            buildingToModelConverter.convert(building, game.getGameId()),
            constructionToModelConverter.convert(construction, game.getGameId()),
            constructionProcess.toModel()
        );

        return surfaceToResponseConverter.convert(surface);
    }
}
