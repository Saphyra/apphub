package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct.CancelDeconstructionService;
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
    private final BuildingDeconstructionToQueueItemConverter buildingDeconstructionToQueueItemConverter;
    private final CancelDeconstructionService cancelDeconstructionService;
    private final PriorityValidator priorityValidator;
    private final DeconstructionConverter deconstructionConverter;

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

                game.getProgressDiff()
                    .save(deconstructionConverter.toModel(game.getGameId(), deconstruction));
            })
            .getOrThrow();
    }

    @Override
    public void cancel(UUID userId, UUID planetId, UUID deconstructionId) {
        cancelDeconstructionService.cancelDeconstructionOfDeconstruction(userId, deconstructionId);
    }
}
