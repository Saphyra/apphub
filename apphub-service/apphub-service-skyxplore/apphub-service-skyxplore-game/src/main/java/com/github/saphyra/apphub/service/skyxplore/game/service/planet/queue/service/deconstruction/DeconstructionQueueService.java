package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct.CancelDeconstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.DeconstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructionQueueService implements QueueService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;
    private final DeconstructionToModelConverter deconstructionToModelConverter;
    private final QueueItemToResponseConverter queueItemToResponseConverter;
    private final BuildingDeconstructionToQueueItemConverter buildingDeconstructionToQueueItemConverter;
    private final WsMessageSender messageSender;
    private final CancelDeconstructionService cancelDeconstructionService;
    private final PriorityValidator priorityValidator;

    @Override
    public List<QueueItem> getQueue(GameData gameData, UUID location) {
        return gameData.getDeconstructions()
            .getByLocation(location)
            .stream()
            .map(deconstruction -> buildingDeconstructionToQueueItemConverter.convert(gameData, deconstruction))
            .toList();
    }

    @Override
    public QueueItemType getType() {
        return QueueItemType.DECONSTRUCTION;
    }

    @Override
    public void setPriority(UUID userId, UUID planetId, UUID deconstructionId, Integer priority) {
        priorityValidator.validate(priority);

        Game game = gameDao.findByUserIdValidated(userId);
        Deconstruction deconstruction = game.getData()
            .getDeconstructions()
            .findByDeconstructionId(deconstructionId);

        game.getEventLoop()
            .processWithWait(() -> {
                deconstruction.setPriority(priority);
                gameDataProxy.saveItem(deconstructionToModelConverter.convert(game.getGameId(), deconstruction));
                QueueResponse queueResponse = queueItemToResponseConverter.convert(buildingDeconstructionToQueueItemConverter.convert(game.getData(), deconstruction), game.getData(), planetId);
                messageSender.planetQueueItemModified(userId, planetId, queueResponse);
            })
            .getOrThrow();
    }

    @Override
    public void cancel(UUID userId, UUID planetId, UUID deconstructionId) {
        cancelDeconstructionService.cancelDeconstructionOfDeconstruction(userId, planetId, deconstructionId);
    }
}
