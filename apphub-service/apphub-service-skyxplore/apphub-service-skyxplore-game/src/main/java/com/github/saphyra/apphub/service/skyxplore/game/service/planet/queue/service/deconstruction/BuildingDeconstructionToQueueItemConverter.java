package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BuildingDeconstructionToQueueItemConverter {
    private final GameProperties gameProperties;

    public QueueItem convert(Building building) {
        Deconstruction deconstruction = building.getDeconstruction();
        if (isNull(deconstruction)) {
            return null;
        }
        log.info("After update: {}", deconstruction);

        return QueueItem.builder()
            .itemId(deconstruction.getDeconstructionId())
            .type(QueueItemType.CONSTRUCTION)
            .requiredWorkPoints(gameProperties.getDeconstruction().getRequiredWorkPoints())
            .currentWorkPoints(deconstruction.getCurrentWorkPoints())
            .priority(deconstruction.getPriority())
            .build();
    }
}
