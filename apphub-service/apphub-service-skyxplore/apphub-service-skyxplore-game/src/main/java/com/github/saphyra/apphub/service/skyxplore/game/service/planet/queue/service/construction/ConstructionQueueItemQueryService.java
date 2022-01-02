package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionQueueItemQueryService {
    private final GameDao gameDao;

    List<QueueItem> getQueue(Planet planet) {
        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> nonNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .filter(building -> nonNull(building.getConstruction()))
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private QueueItem convert(Building building) {
        Construction construction = building.getConstruction();

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
