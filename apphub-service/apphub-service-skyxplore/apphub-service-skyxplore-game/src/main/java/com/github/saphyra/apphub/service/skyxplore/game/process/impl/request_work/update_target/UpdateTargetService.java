package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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

    public void updateTarget(SyncCache syncCache, RequestWorkProcessType processType, Game game, Planet planet, UUID targetId, int completedWorkPoints) {
        switch (processType) {
            case CONSTRUCTION:
                constructionUpdateService.updateConstruction(syncCache, game, planet, targetId, completedWorkPoints);
                break;
            case TERRAFORMATION:
                terraformationUpdateService.updateTerraformation(syncCache, game, planet, targetId, completedWorkPoints);
                break;
            case OTHER:
                log.info("No status update needed.");
                break;
            default:
                throw ExceptionFactory.reportedException("No handler for requestWorkProcessType " + processType);
        }
    }
}
