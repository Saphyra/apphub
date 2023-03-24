package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform.CancelTerraformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
class TerraformationQueueService implements QueueService {
    private final TerraformationQueueItemQueryService terraformationQueueItemQueryService;
    private final CancelTerraformationService cancelTerraformationService;
    private final TerraformationQueueItemPriorityUpdateService priorityUpdateService;

    @Override
    public List<QueueItem> getQueue(GameData gameData, UUID location) {
        return terraformationQueueItemQueryService.getQueue(gameData, location);
    }

    @Override
    public QueueItemType getType() {
        return QueueItemType.TERRAFORMATION;
    }

    @Override
    public void setPriority(UUID userId, UUID planetId, UUID itemId, Integer priority) {
        priorityUpdateService.updatePriority(userId, planetId, itemId, priority);
    }

    @Override
    public void cancel(UUID userId, UUID planetId, UUID itemId) {
        cancelTerraformationService.cancelTerraformationQueueItem(userId, planetId, itemId);
    }
}
