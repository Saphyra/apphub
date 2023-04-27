package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingConstructionToQueueItemConverter {
    public QueueItem convert(GameData gameData, Construction construction) {
        Building building = gameData.getBuildings()
            .findByBuildingId(construction.getExternalReference());

        return QueueItem.builder()
            .itemId(construction.getConstructionId())
            .type(QueueItemType.CONSTRUCTION)
            .requiredWorkPoints(construction.getRequiredWorkPoints())
            .currentWorkPoints(construction.getCurrentWorkPoints())
            .priority(construction.getPriority())
            .data(Map.of(
                "dataId", building.getDataId(),
                "currentLevel", building.getLevel()
            ))
            .build();
    }
}
