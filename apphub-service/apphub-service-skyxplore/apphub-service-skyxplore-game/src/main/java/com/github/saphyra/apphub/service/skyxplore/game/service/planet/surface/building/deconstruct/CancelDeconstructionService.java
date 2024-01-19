package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDeconstructionService {
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
        game.getEventLoop()
            .processWithWait(() -> {

                game.getData()
                    .getProcesses()
                    .findByExternalReferenceAndTypeValidated(deconstruction.getDeconstructionId(), ProcessType.DECONSTRUCTION)
                    .cleanup();

                game.getData()
                    .getDeconstructions()
                    .remove(deconstruction);

                game.getProgressDiff()
                    .delete(deconstruction.getDeconstructionId(), GameItemType.DECONSTRUCTION);
            })
            .getOrThrow();
    }
}
