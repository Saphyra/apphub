package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
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

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
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
        Planet planet = game.getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByBuildingIdValidated(buildingId);
        Building building = surface.getBuilding();

        if (!isNull(building.getConstruction())) {
            throw ExceptionFactory.forbiddenOperation(buildingId + " is under construction");
        }

        Deconstruction deconstruction = deconstructionFactory.create(buildingId);

        return game.getEventLoop()
            .processWithResponseAndWait(() -> {
                building.setDeconstruction(deconstruction);

                QueueResponse queueResponse = queueItemToResponseConverter.convert(buildingDeconstructionToQueueItemConverter.convert(building), planet);
                messageSender.planetQueueItemModified(userId, planetId, queueResponse);
                messageSender.planetBuildingDetailsModified(userId, planetId, planetBuildingOverviewQueryService.getBuildingOverview(planet));

                DeconstructionProcess process = deconstructionProcessFactory.create(game, planet, deconstruction);

                game.getProcesses()
                    .add(process);

                gameDataProxy.saveItem(
                    buildingToModelConverter.convert(building, game.getGameId()),
                    deconstructionToModelConverter.convert(deconstruction, game.getGameId()),
                    process.toModel()
                );

                return surfaceToResponseConverter.convert(surface);
            })
            .getOrThrow();
    }
}
