package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction.BuildingDeconstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.DeconstructionToModelConverter;
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
class DeconstructionUpdateService {
    private final DeconstructionToModelConverter deconstructionToModelConverter;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final BuildingDeconstructionToQueueItemConverter buildingDeconstructionToQueueItemConverter;
    private final QueueItemToResponseConverter queueItemToResponseConverter;

    void updateDeconstruction(SyncCache syncCache, Game game, Planet planet, UUID deconstructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to DECONSTRUCTION {}", completedWorkPoints, deconstructionId);

        Surface surface = planet.getSurfaces()
            .values()
            .stream()
            .filter(s -> !isNull(s.getBuilding()))
            .filter(s -> !isNull(s.getBuilding().getDeconstruction()))
            .filter(s -> s.getBuilding().getDeconstruction().getDeconstructionId().equals(deconstructionId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(
                HttpStatus.NOT_FOUND,
                ErrorCode.DATA_NOT_FOUND,
                "Target not found with id " + deconstructionId + " for type DECONSTRUCTION on planet " + planet.getPlanetId() + " in game " + game.getGameId()
            ));

        Building building = surface.getBuilding();
        Deconstruction deconstruction = building.getDeconstruction();

        log.info("Before update: {}", deconstruction);
        deconstruction.setCurrentWorkPoints(deconstruction.getCurrentWorkPoints() + completedWorkPoints);
        log.info("After update: {}", deconstruction);

        syncCache.saveGameItem(deconstructionToModelConverter.convert(deconstruction, game.getGameId()));

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED,
            surface.getSurfaceId(),
            () -> messageSender.planetSurfaceModified(planet.getOwner(), planet.getPlanetId(), surfaceToResponseConverter.convert(surface))
        );

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED,
            deconstruction.getDeconstructionId(),
            () -> messageSender.planetQueueItemModified(
                planet.getOwner(),
                planet.getPlanetId(),
                queueItemToResponseConverter.convert(buildingDeconstructionToQueueItemConverter.convert(building), planet)
            )
        );
    }
}
