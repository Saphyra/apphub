package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SurfaceToQueueItemConverter {
    public QueueItem convert(Construction terraformation, Surface surface) {

        return QueueItem.builder()
            .itemId(terraformation.getConstructionId())
            .type(QueueItemType.TERRAFORMATION)
            .requiredWorkPoints(terraformation.getRequiredWorkPoints())
            .currentWorkPoints(terraformation.getCurrentWorkPoints())
            .priority(terraformation.getPriority())
            .data(Map.of(
                "currentSurfaceType", surface.getSurfaceType().name(),
                "targetSurfaceType", terraformation.getData()
            ))
            .build();
    }
}
