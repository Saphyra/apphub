package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation.TerraformationProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation.TerraformationProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation.SurfaceToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class TerraformationService {
    private final TerraformingPossibilitiesService terraformingPossibilitiesService;
    private final GameDao gameDao;
    private final ResourceAllocationService resourceAllocationService;
    private final ConstructionFactory constructionFactory;
    private final GameDataProxy gameDataProxy;
    private final ConstructionToModelConverter constructionToModelConverter;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final SurfaceToQueueItemConverter surfaceToQueueItemConverter;
    private final QueueItemToResponseConverter queueItemToResponseConverter;
    private final WsMessageSender messageSender;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;
    private final TerraformationProcessFactory terraformationProcessFactory;

    SurfaceResponse terraform(UUID userId, UUID planetId, UUID surfaceId, String surfaceTypeString) {
        SurfaceType surfaceType = ValidationUtil.convertToEnumChecked(surfaceTypeString, SurfaceType::valueOf, "surfaceType");

        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game.getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByIdValidated(surfaceId);

        if (nonNull(surface.getTerraformation())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Terraformation already in progress on surface " + surfaceId);
        }

        if (nonNull(surface.getBuilding())) {
            throw ExceptionFactory.forbiddenOperation("There is already a building on surface " + surfaceId);
        }

        ConstructionRequirements constructionRequirements = terraformingPossibilitiesService.getOptional(surface.getSurfaceType())
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed."))
            .stream()
            .filter(terraformingPossibilities -> terraformingPossibilities.getSurfaceType() == surfaceType)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed to " + surfaceType))
            .getConstructionRequirements();

        Construction construction = constructionFactory.create(surfaceId, constructionRequirements.getParallelWorkers(), constructionRequirements.getRequiredWorkPoints(), surfaceTypeString);

        resourceAllocationService.processResourceRequirements(game.getGameId(), planet, LocationType.PLANET, construction.getConstructionId(), constructionRequirements.getRequiredResources());

        surface.setTerraformation(construction);

        QueueResponse queueResponse = queueItemToResponseConverter.convert(surfaceToQueueItemConverter.convert(surface), planet);
        messageSender.planetQueueItemModified(userId, planetId, queueResponse);
        messageSender.planetBuildingDetailsModified(userId, planetId, planetBuildingOverviewQueryService.getBuildingOverview(planet));

        TerraformationProcess constructionProcess = terraformationProcessFactory.create(game, planet, surface);

        Processes processes = game.getProcesses();
        synchronized (processes) {
            processes.add(constructionProcess);
        }

        gameDataProxy.saveItem(
            constructionToModelConverter.convert(construction, game.getGameId()),
            constructionProcess.toModel()
        );

        return surfaceToResponseConverter.convert(surface);
    }
}
