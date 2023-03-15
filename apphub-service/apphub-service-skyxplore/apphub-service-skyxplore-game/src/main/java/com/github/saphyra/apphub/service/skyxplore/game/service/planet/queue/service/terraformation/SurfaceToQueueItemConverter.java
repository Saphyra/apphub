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
    public QueueItem convert(Surface surface) {
        Construction construction = surface.getTerraformation();

        return QueueItem.builder()
            .itemId(construction.getConstructionId())
            .type(QueueItemType.TERRAFORMATION)
            .requiredWorkPoints(construction.getRequiredWorkPoints())
            .currentWorkPoints(construction.getCurrentWorkPoints())
            .priority(construction.getPriority())
            .data(CollectionUtils.toMap(
                new BiWrapper<>("currentSurfaceType", surface.getSurfaceType().name()),
                new BiWrapper<>("targetSurfaceType", construction.getData())
            ))
            .build();
    }
}
