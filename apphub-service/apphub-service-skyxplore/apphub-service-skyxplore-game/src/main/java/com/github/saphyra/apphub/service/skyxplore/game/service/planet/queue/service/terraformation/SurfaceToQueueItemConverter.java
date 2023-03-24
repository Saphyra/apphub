package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
            .data(CollectionUtils.toMap(
                new BiWrapper<>("currentSurfaceType", surface.getSurfaceType().name()),
                new BiWrapper<>("targetSurfaceType", terraformation.getData())
            ))
            .build();
    }
}
