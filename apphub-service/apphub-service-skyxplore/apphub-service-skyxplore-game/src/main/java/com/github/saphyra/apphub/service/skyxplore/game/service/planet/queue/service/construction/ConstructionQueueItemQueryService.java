package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
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
    private final BuildingConstructionToQueueItemConverter converter;

    List<QueueItem> getQueue(Planet planet) {
        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> nonNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .filter(building -> nonNull(building.getConstruction()))
            .map(converter::convert)
            .collect(Collectors.toList());
    }
}
