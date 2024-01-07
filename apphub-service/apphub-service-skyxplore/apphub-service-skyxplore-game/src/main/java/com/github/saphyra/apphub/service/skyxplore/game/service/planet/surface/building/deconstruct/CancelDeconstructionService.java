package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDeconstructionService {
    private final SyncCacheFactory syncCacheFactory;
    private final GameDao gameDao;

    public void cancelDeconstructionOfDeconstruction(UUID userId, UUID deconstructionId) {
        Game game = gameDao.findByUserIdValidated(userId);

        Deconstruction deconstruction = game.getData()
            .getDeconstructions()
            .findByDeconstructionId(deconstructionId);

        processCancellation(game, deconstruction);
    }

    public void cancelDeconstructionOfBuilding(UUID userId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);

        Deconstruction deconstruction = game.getData()
            .getDeconstructions()
            .findByExternalReferenceValidated(buildingId);

        processCancellation(game, deconstruction);
    }

    private void processCancellation(Game game, Deconstruction deconstruction) {
        SyncCache syncCache = syncCacheFactory.create();
        game.getEventLoop()
            .processWithWait(() -> {

                    game.getData()
                        .getProcesses()
                        .findByExternalReferenceAndTypeValidated(deconstruction.getDeconstructionId(), ProcessType.DECONSTRUCTION)
                        .cleanup(syncCache);

                    game.getData()
                        .getDeconstructions()
                        .remove(deconstruction);

                    syncCache.deleteGameItem(deconstruction.getDeconstructionId(), GameItemType.DECONSTRUCTION);
                },
                syncCache
            )
            .getOrThrow();
    }
}
