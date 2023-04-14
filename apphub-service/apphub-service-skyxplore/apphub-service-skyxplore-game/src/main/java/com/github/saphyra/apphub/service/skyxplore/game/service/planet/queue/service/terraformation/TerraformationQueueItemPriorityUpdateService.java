package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TerraformationQueueItemPriorityUpdateService {
    private final GameDao gameDao;
    private final SyncCacheFactory syncCacheFactory;

    public void updatePriority(UUID userId, UUID planetId, UUID constructionId, Integer priority) {
        ValidationUtil.atLeast(priority, 1, "priority");
        ValidationUtil.maximum(priority, 10, "priority");

        Game game = gameDao.findByUserIdValidated(userId);

        Construction terraformation = game.getData()
            .getConstructions()
            .findByConstructionIdValidated(constructionId);

        Surface surface = game.getData()
            .getSurfaces()
            .findBySurfaceId(terraformation.getExternalReference());

        SyncCache syncCache = syncCacheFactory.create(game);

        game.getEventLoop()
            .processWithWait(() -> {
                terraformation.setPriority(priority);

                syncCache.terraformationModified(userId, planetId, terraformation, surface);
            }, syncCache)
            .getOrThrow();
    }
}
