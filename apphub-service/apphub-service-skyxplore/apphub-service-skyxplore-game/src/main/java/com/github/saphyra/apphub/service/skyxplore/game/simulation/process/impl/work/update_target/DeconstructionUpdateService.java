package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructionUpdateService {
    private final DeconstructionConverter deconstructionConverter;

    void updateDeconstruction(SyncCache syncCache, GameData gameData, UUID deconstructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to DECONSTRUCTION {}", completedWorkPoints, deconstructionId);

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionId(deconstructionId);

        log.info("Before update: {}", deconstruction);
        deconstruction.increaseWorkPoints(completedWorkPoints);
        log.info("After update: {}", deconstruction);

        syncCache.saveGameItem(deconstructionConverter.toModel(gameData.getGameId(), deconstruction));
    }
}
