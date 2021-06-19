package com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriorityUpdateService {
    private final GameDao gameDao;
    private final PriorityValidator priorityValidator;

    public void updatePriority(UUID userId, UUID planetId, String priorityTypeString, Integer newPriority) {
        priorityValidator.validate(newPriority);

        PriorityType priorityType = Optional.ofNullable(PriorityType.fromValue(priorityTypeString))
            .orElseThrow(() -> ExceptionFactory.invalidParam("priorityType", "unknown value"));

        gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getPriorities()
            .put(priorityType, newPriority);
    }
}
