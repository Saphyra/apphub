package com.github.saphyra.apphub.service.skyxplore.game.service.planet.command;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PriorityUpdateService {
    private final GameDao gameDao;

    public void updatePriority(UUID userId, UUID planetId, String priorityTypeString, Integer newPriority) {
        //TODO validate newPriority

        PriorityType priorityType = Optional.ofNullable(PriorityType.fromValue(priorityTypeString))
            .orElseThrow(() -> new RuntimeException("No PriorityType exists with name " + priorityTypeString)); //TODO proper exception

        gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getPriorities()
            .put(priorityType, newPriority);
    }
}
