package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionQueueItemPriorityUpdateService {
    private final GameDao gameDao;
    private final ConstructionConverter constructionConverter;

    public void updatePriority(UUID userId, UUID constructionId, Integer priority) {
        ValidationUtil.atLeast(priority, 1, "priority");
        ValidationUtil.maximum(priority, 10, "priority");

        Game game = gameDao.findByUserIdValidated(userId);
        Construction construction = game.getData()
            .getConstructions()
            .findByConstructionIdValidated(constructionId);

        game.getEventLoop()
            .processWithWait(() -> {
                construction.setPriority(priority);

                game.getProgressDiff()
                    .save(constructionConverter.toModel(game.getGameId(), construction));
            })
            .getOrThrow();
    }
}
