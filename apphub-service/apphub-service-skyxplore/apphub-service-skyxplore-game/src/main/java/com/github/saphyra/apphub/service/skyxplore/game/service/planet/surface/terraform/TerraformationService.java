package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
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
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
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
        GameData gameData = game.getData();

        Planet planet = gameData.getPlanets()
            .get(planetId);

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(surfaceId);

        if (gameData.getConstructions().findByExternalReference(surfaceId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Terraformation already in progress on surface " + surfaceId);
        }

        if (gameData.getBuildings().findBySurfaceId(surfaceId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation("There is already a building on surface " + surfaceId);
        }

        ConstructionRequirements constructionRequirements = terraformingPossibilitiesService.getOptional(surface.getSurfaceType())
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed."))
            .stream()
            .filter(terraformingPossibilities -> terraformingPossibilities.getSurfaceType() == surfaceType)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed to " + surfaceType))
            .getConstructionRequirements();

        Construction construction = constructionFactory.create(
            surfaceId,
            ConstructionType.TERRAFORMATION,
            planetId,
            constructionRequirements.getParallelWorkers(),
            constructionRequirements.getRequiredWorkPoints(),
            surfaceTypeString
        );

        return game.getEventLoop()
            .processWithResponseAndWait(() -> {
                resourceAllocationService.processResourceRequirements(gameData, planetId, planet.getOwner(), construction.getConstructionId(), constructionRequirements.getRequiredResources());

                gameData.getConstructions()
                    .add(construction);

                QueueResponse queueResponse = queueItemToResponseConverter.convert(surfaceToQueueItemConverter.convert(construction, surface), gameData, planetId);
                messageSender.planetQueueItemModified(userId, planetId, queueResponse);
                messageSender.planetBuildingDetailsModified(userId, planetId, planetBuildingOverviewQueryService.getBuildingOverview(gameData, planetId));

                TerraformationProcess constructionProcess = terraformationProcessFactory.create(gameData, planetId, surface);

                gameData.getProcesses()
                    .add(constructionProcess);

                gameDataProxy.saveItem(
                    constructionToModelConverter.convert(game.getGameId(), construction),
                    constructionProcess.toModel()
                );

                return surfaceToResponseConverter.convert(gameData, surface);
            })
            .getOrThrow();
    }
}
