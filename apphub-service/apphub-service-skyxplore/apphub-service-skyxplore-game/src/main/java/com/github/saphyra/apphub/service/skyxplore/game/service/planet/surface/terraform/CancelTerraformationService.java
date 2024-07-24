package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelTerraformationService {
    private final GameDao gameDao;
    private final AllocationRemovalService allocationRemovalService;

    public void cancelTerraformationQueueItem(UUID userId, UUID constructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Construction terraformation = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        processCancellation(userId, game, terraformation);
    }

    void cancelTerraformationOfSurface(UUID userId, UUID surfaceId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Construction terraformation = gameData.getConstructions()
            .findByExternalReferenceValidated(surfaceId);

        processCancellation(userId, game, terraformation);
    }

    @SneakyThrows
    private void processCancellation(UUID userId, Game game, Construction terraformation) {
        if (!userId.equals(game.getData().getPlanets().findByIdValidated(terraformation.getLocation()).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot cancel terraformation on planet " + terraformation.getConstructionId());
        }

        game.getEventLoop()
            .processWithWait(() -> {
                game.getData()
                    .getProcesses()
                    .findByExternalReferenceAndTypeValidated(terraformation.getConstructionId(), ProcessType.TERRAFORMATION)
                    .cleanup();

                game.getData()
                    .getConstructions()
                    .remove(terraformation);

                allocationRemovalService.removeAllocationsAndReservations(game.getProgressDiff(), game.getData(), terraformation.getConstructionId());

                game.getProgressDiff()
                    .delete(terraformation.getConstructionId(), GameItemType.CONSTRUCTION);
            })
            .getOrThrow();
    }
}
