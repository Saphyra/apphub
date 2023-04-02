package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriorityFillerService {
    private final IdGenerator idGenerator;

    public void fillPriorities(GameData gameData) {
        gameData.getPlanets()
            .forEach((uuid, planet) -> fillPriorities(planet, gameData));
    }

    private void fillPriorities(Planet planet, GameData gameData) {
        Arrays.stream(PriorityType.values())
            .map(priorityType -> Priority.builder()
                .priorityId(idGenerator.randomUuid())
                .location(planet.getPlanetId())
                .type(priorityType)
                .value(GameConstants.DEFAULT_PRIORITY)
                .build())
            .forEach(priority -> gameData.getPriorities().add(priority));
    }
}
