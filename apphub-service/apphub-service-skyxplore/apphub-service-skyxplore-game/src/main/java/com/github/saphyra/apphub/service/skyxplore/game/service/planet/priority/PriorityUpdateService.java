package com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriorityUpdateService {
    private final GameDao gameDao;
    private final PriorityValidator priorityValidator;

    public void updatePriority(UUID userId, UUID planetId, String priorityTypeString, Integer newPriority) {
        priorityValidator.validate(newPriority);

        PriorityType priorityType = Optional.ofNullable(PriorityType.fromValue(priorityTypeString))
            .orElseThrow(() -> new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "priorityType", "unknown value"), "No PriorityType exists with name " + priorityTypeString));

        gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getPriorities()
            .put(priorityType, newPriority);
    }
}
