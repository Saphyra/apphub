package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class TerraformationQueueItemQueryService {
    private final TerraformationToQueueItemConverter converter;

    List<QueueItem> getQueue(GameData gameData, UUID location) {
        return gameData.getConstructions()
            .getByLocationAndType(location, ConstructionType.TERRAFORMATION)
            .stream()
            .map(construction -> converter.convert(construction, gameData.getSurfaces().findByIdValidated(construction.getExternalReference())))
            .collect(Collectors.toList());
    }
}
