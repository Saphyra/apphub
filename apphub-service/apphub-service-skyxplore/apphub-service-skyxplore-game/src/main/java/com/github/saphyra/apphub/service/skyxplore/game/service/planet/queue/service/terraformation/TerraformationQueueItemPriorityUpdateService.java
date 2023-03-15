package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
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
class TerraformationQueueItemPriorityUpdateService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;
    private final ConstructionToModelConverter constructionToModelConverter;
    private final WsMessageSender messageSender;
    private final SurfaceToQueueItemConverter surfaceToQueueItemConverter;
    private final QueueItemToResponseConverter queueItemToResponseConverter;

    public void updatePriority(UUID userId, UUID planetId, UUID constructionId, Integer priority) {
        ValidationUtil.atLeast(priority, 1, "priority");
        ValidationUtil.maximum(priority, 10, "priority");

        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game.getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet
            .getSurfaces()
            .values()
            .stream()
            .filter(s -> nonNull(s.getTerraformation()))
            .filter(c -> c.getTerraformation().getConstructionId().equals(constructionId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Surface not found with terraformation constructionId " + constructionId));
        Construction construction = surface.getTerraformation();

        game.getEventLoop()
            .processWithWait(() -> {
                construction.setPriority(priority);
                gameDataProxy.saveItem(constructionToModelConverter.convert(construction, game.getGameId()));
                QueueResponse queueResponse = queueItemToResponseConverter.convert(surfaceToQueueItemConverter.convert(surface), planet);
                messageSender.planetQueueItemModified(userId, planetId, queueResponse);
            })
            .getOrThrow();
    }
}
