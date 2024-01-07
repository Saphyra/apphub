package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionUpdateService {
    private final ConstructionConverter constructionConverter;

    void updateConstruction(SyncCache syncCache, GameData gameData, UUID constructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to CONSTRUCTION {}", completedWorkPoints, constructionId);

        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        log.info("Before update: {}", construction);
        construction.increaseCurrentWorkPoints(completedWorkPoints);
        log.info("After update: {}", construction);

        syncCache.saveGameItem(constructionConverter.toModel(gameData.getGameId(), construction));
    }
}
