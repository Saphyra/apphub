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
class TerraformationUpdateService {
    private final ConstructionConverter constructionConverter;

    void updateTerraformation(SyncCache syncCache, GameData gameData, UUID constructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to TERRAFORMATION {}", completedWorkPoints, constructionId);

        Construction terraformation = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        log.info("Before update: {}", terraformation);
        terraformation.increaseCurrentWorkPoints(completedWorkPoints);
        log.info("After update: {}", terraformation);

        syncCache.saveGameItem(constructionConverter.toModel(gameData.getGameId(), terraformation));
    }
}
