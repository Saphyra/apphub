package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.common;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDeconstructionFacade {
    private final GameDao gameDao;

    public void cancelDeconstructionOfConstructionArea(UUID userId, UUID deconstructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionIdValidated(deconstructionId);

        if (!userId.equals(game.getData().getPlanets().findByIdValidated(deconstruction.getLocation()).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot cancel deconstruction of constructionArea on planet " + deconstruction.getDeconstructionId());
        }

        game.getEventLoop()
            .processWithWait(() -> cancelDeconstructionOfConstructionAreaAndBuildingModules(game, deconstruction))
            .getOrThrow();
    }

    public UUID cancelDeconstructionOfBuildingModule(UUID userId, UUID deconstructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionIdValidated(deconstructionId);

        if (!userId.equals(game.getData().getPlanets().findByIdValidated(deconstruction.getLocation()).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot cancel deconstruction of constructionArea on planet " + deconstructionId);
        }

        UUID constructionAreaId = gameData.getBuildingModules()
            .findByIdValidated(deconstruction.getExternalReference())
            .getConstructionAreaId();

        game.getEventLoop()
            .processWithWait(() -> {
                cancelDeconstructionOfBuildingModule(game, deconstruction);
                gameData.getDeconstructions()
                    .findByExternalReference(constructionAreaId)
                    .ifPresentOrElse(
                        constructionAreaDeconstruction -> cancelDeconstructionOfConstructionAreaAndBuildingModules(game, constructionAreaDeconstruction),
                        () -> log.info("ConstructionArea {} is not under deconstruction, nothing to cancel.", constructionAreaId)
                    );
            })
            .getOrThrow();

        return constructionAreaId;
    }

    private void cancelDeconstructionOfConstructionAreaAndBuildingModules(Game game, Deconstruction deconstruction) {
        GameData gameData = game.getData();
        UUID deconstructionId = deconstruction.getDeconstructionId();

        gameData.getProcesses()
            .findByExternalReferenceAndTypeValidated(deconstructionId, ProcessType.DECONSTRUCT_CONSTRUCTION_AREA)
            .cleanup();

        gameData.getDeconstructions()
            .remove(deconstruction);

        game.getProgressDiff()
            .delete(deconstructionId, GameItemType.DECONSTRUCTION);
        cancelDeconstructionOfConstructionAreaBuildingModules(game, deconstruction.getExternalReference());
    }

    private void cancelDeconstructionOfConstructionAreaBuildingModules(Game game, UUID constructionAreaId) {
        GameData gameData = game.getData();

        List<BuildingModule> buildingModulesOfConstructionArea = gameData.getBuildingModules()
            .getByConstructionAreaId(constructionAreaId);
        buildingModulesOfConstructionArea.stream()
            .map(buildingModule -> gameData.getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(deconstruction -> cancelDeconstructionOfBuildingModule(game, deconstruction));
    }

    private static void cancelDeconstructionOfBuildingModule(Game game, Deconstruction deconstruction) {
        GameData gameData = game.getData();

        gameData.getProcesses()
            .findByExternalReferenceAndTypeValidated(deconstruction.getDeconstructionId(), ProcessType.DECONSTRUCT_BUILDING_MODULE)
            .cleanup();

        gameData.getDeconstructions()
            .remove(deconstruction);

        game.getProgressDiff()
            .delete(deconstruction.getDeconstructionId(), GameItemType.DECONSTRUCTION);
    }
}
