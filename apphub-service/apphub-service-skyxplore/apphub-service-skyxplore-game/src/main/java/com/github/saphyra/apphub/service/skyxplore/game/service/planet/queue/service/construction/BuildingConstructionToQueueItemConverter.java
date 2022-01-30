package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingConstructionToQueueItemConverter {
    public QueueItem convert(Building building) {
        Construction construction = building.getConstruction();
        if (isNull(construction)) {
            return null; //TODO unit test
        }
        log.info("After update: {}", construction);

        return QueueItem.builder()
            .itemId(construction.getConstructionId())
            .type(QueueItemType.CONSTRUCTION)
            .requiredWorkPoints(construction.getRequiredWorkPoints())
            .currentWorkPoints(construction.getCurrentWorkPoints())
            .priority(construction.getPriority())
            .data(CollectionUtils.toMap(
                new BiWrapper<>("dataId", building.getDataId()),
                new BiWrapper<>("currentLevel", building.getLevel())
            ))
            .build();
    }
}
