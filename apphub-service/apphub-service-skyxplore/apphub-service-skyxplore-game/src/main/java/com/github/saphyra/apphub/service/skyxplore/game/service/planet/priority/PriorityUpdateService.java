package com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
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
public class PriorityUpdateService {
    private final GameDao gameDao;

    public void updatePriority(UUID userId, UUID planetId, String priorityTypeString, Integer newPriority) {
        PriorityType priorityType = Optional.ofNullable(PriorityType.fromValue(priorityTypeString))
            .orElseThrow(() -> new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "priorityType", "unknown value"), "No PriorityType exists with name " + priorityTypeString));

        if (newPriority < 1) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "value", "too low"), newPriority + " is too small");
        }

        if (newPriority > 10) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "value", "too high"), newPriority + " is too high");
        }

        gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getPriorities()
            .put(priorityType, newPriority);
    }
}
