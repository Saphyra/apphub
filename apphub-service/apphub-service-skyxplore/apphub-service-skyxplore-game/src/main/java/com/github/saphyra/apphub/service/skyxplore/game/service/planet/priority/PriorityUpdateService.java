package com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityConverter;
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
    private final PriorityConverter priorityConverter;
    private final GameDataProxy gameDataProxy;

    public void updatePriority(UUID userId, UUID planetId, String priorityTypeString, Integer newPriority) {
        priorityValidator.validate(newPriority);

        PriorityType priorityType = Optional.ofNullable(PriorityType.fromValue(priorityTypeString))
            .orElseThrow(() -> ExceptionFactory.invalidParam("priorityType", "unknown value"));

        Game game = gameDao.findByUserIdValidated(userId);
        Priority priority = game.getData()
            .getPriorities()
            .findByLocationAndType(planetId, priorityType);

        game.getEventLoop()
            .processWithWait(() -> {
                priority.setValue(newPriority);

                PriorityModel model = priorityConverter.toModel(game.getGameId(), priority);
                gameDataProxy.saveItem(model); //TODO do not save directly
            })
            .getOrThrow();
    }
}
