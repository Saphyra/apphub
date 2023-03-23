package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueItemToResponseConverter {
    public QueueResponse convert(QueueItem queueItem, GameData gameData, UUID location) {
        if (isNull(queueItem)) {
            return null;
        }

        int prioritySetting = gameData.getPriorities()
            .findByLocationAndType(location, queueItem.getType().getPriorityType())
            .getValue();

        return QueueResponse.builder()
            .itemId(queueItem.getItemId())
            .type(queueItem.getType().name())
            .requiredWorkPoints(queueItem.getRequiredWorkPoints())
            .currentWorkPoints(queueItem.getCurrentWorkPoints())
            .ownPriority(queueItem.getPriority())
            .totalPriority(queueItem.getPriority() * prioritySetting)
            .data(queueItem.getData())
            .build();
    }
}
