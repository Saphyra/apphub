package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionQueueItemQueryService {
    private final BuildingConstructionToQueueItemConverter converter;

    public List<QueueItem> getQueue(GameData gameData, UUID location) {
        return gameData.getConstructions()
            .getByLocationAndType(location, ConstructionType.CONSTRUCTION)
            .stream()
            .map(construction -> converter.convert(gameData, construction))
            .toList();
    }
}
