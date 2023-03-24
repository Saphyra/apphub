package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation.SurfaceToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TerraformationUpdateService {
    private final ConstructionToModelConverter constructionToModelConverter;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final QueueItemToResponseConverter queueItemToResponseConverter;
    private final SurfaceToQueueItemConverter surfaceToQueueItemConverter;

    void updateTerraformation(SyncCache syncCache, GameData gameData, UUID location, UUID constructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to TERRAFORMATION {}", completedWorkPoints, constructionId);

        Construction construction = gameData.getConstructions()
            .findByIdValidated(constructionId);

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(construction.getExternalReference());

        log.info("Before update: {}", construction);
        construction.setCurrentWorkPoints(construction.getCurrentWorkPoints() + completedWorkPoints);
        log.info("After update: {}", construction);

        syncCache.saveGameItem(constructionToModelConverter.convert(gameData.getGameId(), construction));

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
            construction.getConstructionId(),
            () -> messageSender.planetQueueItemModified(
                ownerId,
                location,
                queueItemToResponseConverter.convert(surfaceToQueueItemConverter.convert(construction, surface), gameData, location)
            )
        );
    }
}
