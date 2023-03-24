package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction.BuildingDeconstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.DeconstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructionUpdateService {
    private final DeconstructionToModelConverter deconstructionToModelConverter;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final BuildingDeconstructionToQueueItemConverter buildingDeconstructionToQueueItemConverter;
    private final QueueItemToResponseConverter queueItemToResponseConverter;

    void updateDeconstruction(SyncCache syncCache, GameData gameData, UUID location, UUID deconstructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to DECONSTRUCTION {}", completedWorkPoints, deconstructionId);

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionId(deconstructionId);

        Building building = gameData.getBuildings()
            .findByBuildingId(deconstruction.getExternalReference());

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(building.getSurfaceId());

        log.info("Before update: {}", deconstruction);
        deconstruction.setCurrentWorkPoints(deconstruction.getCurrentWorkPoints() + completedWorkPoints);
        log.info("After update: {}", deconstruction);

        syncCache.saveGameItem(deconstructionToModelConverter.convert(gameData.getGameId(), deconstruction));

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED,
            surface.getSurfaceId(),
            () -> messageSender.planetSurfaceModified(
                ownerId,
                location,
                surfaceToResponseConverter.convert(gameData, surface)
            )
        );

        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED,
            deconstruction.getDeconstructionId(),
            () -> messageSender.planetQueueItemModified(
                ownerId,
                location,
                queueItemToResponseConverter.convert(buildingDeconstructionToQueueItemConverter.convert(gameData, deconstruction), gameData, location)
            )
        );
    }
}
