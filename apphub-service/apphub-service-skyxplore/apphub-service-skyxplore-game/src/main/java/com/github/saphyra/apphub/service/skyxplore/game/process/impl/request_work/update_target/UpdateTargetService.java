package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
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

    public void updateTarget(SyncCache syncCache, RequestWorkProcessType processType, GameData gameData, UUID location, UUID targetId, int completedWorkPoints) {
        switch (processType) {
            case CONSTRUCTION -> constructionUpdateService.updateConstruction(syncCache, gameData, location, targetId, completedWorkPoints);
            case DECONSTRUCTION -> deconstructionUpdateService.updateDeconstruction(syncCache, gameData, location, targetId, completedWorkPoints);
            case TERRAFORMATION -> terraformationUpdateService.updateTerraformation(syncCache, gameData, location, targetId, completedWorkPoints);
            case OTHER -> log.info("No status update needed.");
            default -> throw ExceptionFactory.reportedException("No handler for requestWorkProcessType " + processType);
        }
    }
}
