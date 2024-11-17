package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CancelDeconstructionOfBuildingModuleService {
    private final GameDao gameDao;

    public UUID cancelDeconstruction(UUID userId, UUID deconstructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionIdValidated(deconstructionId);

        if (!userId.equals(game.getData().getPlanets().findByIdValidated(deconstruction.getLocation()).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot cancel deconstruction of constructionArea on planet " + deconstruction.getDeconstructionId());
        }

        UUID constructionAreaId = gameData.getBuildingModules()
            .findByBuildingModuleIdValidated(deconstruction.getExternalReference())
            .getConstructionAreaId();

        game.getEventLoop()
            .processWithWait(() -> {
                game.getData()
                    .getProcesses()
                    .findByExternalReferenceAndTypeValidated(deconstruction.getDeconstructionId(), ProcessType.DECONSTRUCT_BUILDING_MODULE)
                    .cleanup();

                game.getData()
                    .getDeconstructions()
                    .remove(deconstruction);

                game.getProgressDiff()
                    .delete(deconstruction.getDeconstructionId(), GameItemType.DECONSTRUCTION);
            })
            .getOrThrow();

        return constructionAreaId;
    }
}
