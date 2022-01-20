package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.task_collector.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.tick.planet.task_collector.PlanetTickTaskCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ConstructionTaskCollector implements PlanetTickTaskCollector {
    private final ConstructionTickTaskFactory constructionTickTaskFactory;

    @Override
    public List<TickTask> getTasks(UUID gameId, Planet planet) {
        int constructionPriority = planet.getPriorities().get(PriorityType.CONSTRUCTION);

        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> nonNull(surface.getBuilding()))
            .filter(surface -> nonNull(surface.getBuilding().getConstruction()))
            .map(surface -> constructionTickTaskFactory.create(gameId, planet, surface, constructionPriority))
            .collect(Collectors.toList());
    }
}
