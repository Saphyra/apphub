package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelConstructionAreaConstructionService {
    private final GameDao gameDao;
    private final AllocationRemovalService allocationRemovalService;

    public void cancelConstruction(UUID userId, UUID constructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        if (!userId.equals(game.getData().getPlanets().findByIdValidated(construction.getLocation()).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot cancel construction of constructionArea on planet " + construction.getConstructionId());
        }

        UUID constructionAreaId = construction.getExternalReference();
        ConstructionArea constructionArea = gameData.getConstructionAreas()
            .findByConstructionAreaIdValidated(constructionAreaId);

        game.getEventLoop()
            .processWithWait(() -> {
                gameData.getProcesses()
                    .findByExternalReferenceAndTypeValidated(constructionId, ProcessType.CONSTRUCT_CONSTRUCTION_AREA)
                    .cleanup();

                gameData.getConstructions()
                    .remove(construction);

                allocationRemovalService.removeAllocationsAndReservations(game.getProgressDiff(), gameData, constructionId);

                game.getProgressDiff()
                    .delete(constructionId, GameItemType.CONSTRUCTION);

                gameData.getConstructionAreas()
                    .remove(constructionArea);
                game.getProgressDiff()
                    .delete(constructionAreaId, GameItemType.CONSTRUCTION_AREA);
            })
            .getOrThrow();
    }
}
