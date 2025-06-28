package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
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
class TerraformationQueueItemQueryService {
    private final TerraformationToQueueItemConverter converter;

    List<QueueItem> getQueue(GameData gameData, UUID location) {
        return gameData.getSurfaces()
            .getByPlanetId(location)
            .stream()
            .map(surface -> new BiWrapper<>(surface, gameData.getConstructions().findByExternalReference(surface.getSurfaceId())))
            .filter(bw -> bw.getEntity2().isPresent())
            .map(bw -> bw.mapEntity2(Optional::get))
            .map(bw -> converter.convert(gameData, bw.getEntity2(), bw.getEntity1()))
            .collect(Collectors.toList());
    }
}
