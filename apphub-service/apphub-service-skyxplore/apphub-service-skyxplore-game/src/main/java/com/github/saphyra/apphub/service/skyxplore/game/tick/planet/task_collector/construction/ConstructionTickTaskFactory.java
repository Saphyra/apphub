package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.task_collector.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor.construction.ConstructionTickProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionTickTaskFactory {
    private final ConstructionTickProcessor constructionTickProcessor;

    ConstructionTickTask create(UUID gameId, Planet planet, Surface surface, int constructionPriority) {
        Construction construction = surface.getBuilding()
            .getConstruction();

        return ConstructionTickTask.builder()
            .gameId(gameId)
            .planet(planet)
            .surface(surface)
            .priority(constructionPriority * construction.getPriority())
            .constructionTickProcessor(constructionTickProcessor)
            .build();
    }
}
