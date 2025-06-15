package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class TerraformationQueueItemQueryService {
    private final TerraformationToQueueItemConverter converter;

    List<QueueItem> getQueue(GameData gameData, UUID location) {
        return gameData.getSurfaces()
            .getByPlanetId(location)
            .stream()
            .map(surface -> gameData.getConstructions().findByExternalReference(surface.getSurfaceId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(construction -> converter.convert(gameData, construction, gameData.getSurfaces().findByIdValidated(construction.getExternalReference())))
            .collect(Collectors.toList());
    }
}
