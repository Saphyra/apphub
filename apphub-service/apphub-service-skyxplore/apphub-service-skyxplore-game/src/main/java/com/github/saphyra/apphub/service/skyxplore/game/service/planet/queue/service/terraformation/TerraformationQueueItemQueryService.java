package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
class TerraformationQueueItemQueryService {
    private final SurfaceToQueueItemConverter converter;

    List<QueueItem> getQueue(Planet planet) {
        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> nonNull(surface.getTerraformation()))
            .map(converter::convert)
            .collect(Collectors.toList());
    }
}
