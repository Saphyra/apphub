package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction.DeconstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction.DeconstructionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.DeconstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction.BuildingDeconstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.DeconstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructBuildingService {
    private final GameDao gameDao;
    private final DeconstructionFactory deconstructionFactory;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final BuildingDeconstructionToQueueItemConverter buildingDeconstructionToQueueItemConverter;
    private final QueueItemToResponseConverter queueItemToResponseConverter;
    private final WsMessageSender messageSender;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;
    private final DeconstructionProcessFactory deconstructionProcessFactory;
    private final GameDataProxy gameDataProxy;
    private final BuildingToModelConverter buildingToModelConverter;
    private final DeconstructionToModelConverter deconstructionToModelConverter;

    public SurfaceResponse deconstructBuilding(UUID userId, UUID planetId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);

        if (game.getData().getConstructions().findByExternalReference(buildingId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation(buildingId + " is under construction");
        }

        Deconstruction deconstruction = deconstructionFactory.create(buildingId, planetId);

        Building building = game.getData()
            .getBuildings()
            .findByBuildingId(buildingId);

        Surface surface = game.getData()
            .getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        return game.getEventLoop()
            .processWithResponseAndWait(() -> {
                game.getData()
                    .getDeconstructions()
                    .add(deconstruction);

                QueueResponse queueResponse = queueItemToResponseConverter.convert(buildingDeconstructionToQueueItemConverter.convert(game.getData(), deconstruction), game.getData(), planetId);
                messageSender.planetQueueItemModified(userId, planetId, queueResponse);
                messageSender.planetBuildingDetailsModified(userId, planetId, planetBuildingOverviewQueryService.getBuildingOverview(game.getData(), planetId));

                DeconstructionProcess process = deconstructionProcessFactory.create(game.getData(), planetId, deconstruction);

                game.getData()
                    .getProcesses()
                    .add(process);

                gameDataProxy.saveItem(
                    buildingToModelConverter.convert(game.getGameId(), building),
                    deconstructionToModelConverter.convert(game.getGameId(), deconstruction),
                    process.toModel()
                );

                return surfaceToResponseConverter.convert(game.getData(), surface);
            })
            .getOrThrow();
    }
}
