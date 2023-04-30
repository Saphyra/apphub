package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;

import java.util.List;
import java.util.UUID;


public interface QueueService {
    QueueItemType getType();

    List<QueueItem> getQueue(GameData gameData, UUID location);

    void setPriority(UUID userId, UUID planetId, UUID itemId, Integer priority);

    void cancel(UUID userId, UUID planetId, UUID itemId);
}
