package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.CancelConstructionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
class ConstructionQueueService implements QueueService {
    private final ConstructionQueueItemQueryService constructionQueueItemQueryService;
    private final CancelConstructionService cancelConstructionService;
    private final ConstructionQueueItemPriorityUpdateService constructionQueueItemPriorityUpdateService;

    @Override
    public List<QueueItem> getQueue(GameData gameData, UUID location) {
        return constructionQueueItemQueryService.getQueue(gameData, location);
    }

    @Override
    public QueueItemType getType() {
        return QueueItemType.CONSTRUCTION;
    }

    @Override
    public void setPriority(UUID userId, UUID planetId, UUID itemId, Integer priority) {
        constructionQueueItemPriorityUpdateService.updatePriority(userId, planetId, itemId, priority);
    }

    @Override
    public void cancel(UUID userId, UUID planetId, UUID itemId) {
        cancelConstructionService.cancelConstructionOfConstruction(userId, planetId, itemId);
    }
}
