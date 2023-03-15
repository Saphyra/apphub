package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation.SurfaceToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class TerraformationUpdateService {
    private final ConstructionToModelConverter constructionToModelConverter;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final QueueItemToResponseConverter queueItemToResponseConverter;
    private final SurfaceToQueueItemConverter surfaceToQueueItemConverter;

    void updateTerraformation(SyncCache syncCache, Game game, Planet planet, UUID constructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to TERRAFORMATION {}", completedWorkPoints, constructionId);

        Surface surface = planet.getSurfaces()
            .values()
            .stream()
            .filter(s -> !isNull(s.getTerraformation()))
            .filter(s -> s.getTerraformation().getConstructionId().equals(constructionId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(
                HttpStatus.NOT_FOUND,
                ErrorCode.DATA_NOT_FOUND,
                "Target not found with id " + constructionId + " for type TERRAFORMATION on planet " + planet.getPlanetId() + " in game " + game.getGameId()
            ));

        Construction construction = surface.getTerraformation();

        log.info("Before update: {}", construction);
        construction.setCurrentWorkPoints(construction.getCurrentWorkPoints() + completedWorkPoints);
        log.info("After update: {}", construction);

        syncCache.saveGameItem(constructionToModelConverter.convert(construction, game.getGameId()));

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED,
            surface.getSurfaceId(),
            () -> messageSender.planetSurfaceModified(planet.getOwner(), planet.getPlanetId(), surfaceToResponseConverter.convert(surface))
        );

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED,
            construction.getConstructionId(),
            () -> messageSender.planetQueueItemModified(
                planet.getOwner(),
                planet.getPlanetId(),
                queueItemToResponseConverter.convert(surfaceToQueueItemConverter.convert(surface), planet)
            )
        );
    }
}
