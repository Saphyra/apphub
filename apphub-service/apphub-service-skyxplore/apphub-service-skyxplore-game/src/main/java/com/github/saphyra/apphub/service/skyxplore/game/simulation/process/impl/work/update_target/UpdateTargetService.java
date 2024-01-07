package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateTargetService {
    private final ConstructionUpdateService constructionUpdateService;
    private final TerraformationUpdateService terraformationUpdateService;
    private final DeconstructionUpdateService deconstructionUpdateService;

    public void updateTarget(SyncCache syncCache, GameData gameData, WorkProcessType processType, UUID targetId, int completedWorkPoints) {
        switch (processType) {
            case CONSTRUCTION -> constructionUpdateService.updateConstruction(syncCache, gameData, targetId, completedWorkPoints);
            case DECONSTRUCTION -> deconstructionUpdateService.updateDeconstruction(syncCache, gameData, targetId, completedWorkPoints);
            case TERRAFORMATION -> terraformationUpdateService.updateTerraformation(syncCache, gameData, targetId, completedWorkPoints);
            case OTHER -> log.info("No status update needed.");
            default -> throw ExceptionFactory.reportedException("No handler for requestWorkProcessType " + processType);
        }
    }
}
