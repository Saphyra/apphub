package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class QueueItem {
    private final UUID itemId;
    private final QueueItemType type;
    private final Integer requiredWorkPoints;
    private final Integer currentWorkPoints;
    private final Integer priority;
    private final Map<String, Object> data;
}
