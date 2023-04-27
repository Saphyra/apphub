package com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriorityQueryService {
    private final GameDao gameDao;

    public Map<String, Integer> getPriorities(UUID userId, UUID planetId) {
        return gameDao.findByUserIdValidated(userId)
            .getData()
            .getPriorities()
            .getByLocation(planetId)
            .stream()
            .collect(Collectors.toMap(priority -> priority.getType().name().toLowerCase(), Priority::getValue));
    }
}
