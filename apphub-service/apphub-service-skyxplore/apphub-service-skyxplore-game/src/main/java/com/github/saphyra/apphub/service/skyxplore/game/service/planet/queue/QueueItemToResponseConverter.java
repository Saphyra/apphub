package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueItemToResponseConverter {
    public QueueResponse convert(QueueItem queueItem, Planet planet) {
        if (isNull(queueItem)) {
            return null;
        }

        return QueueResponse.builder()
            .itemId(queueItem.getItemId())
            .type(queueItem.getType().name())
            .requiredWorkPoints(queueItem.getRequiredWorkPoints())
            .currentWorkPoints(queueItem.getCurrentWorkPoints())
            .ownPriority(queueItem.getPriority())
            .totalPriority(queueItem.getPriority() * planet.getPriorities().get(queueItem.getType().getPriorityType()))
            .data(queueItem.getData())
            .build();
    }
}
