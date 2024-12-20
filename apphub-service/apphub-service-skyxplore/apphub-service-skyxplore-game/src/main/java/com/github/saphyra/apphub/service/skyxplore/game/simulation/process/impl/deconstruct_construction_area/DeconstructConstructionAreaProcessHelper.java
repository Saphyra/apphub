package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.DeconstructBuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructConstructionAreaProcessHelper {
    private final WorkProcessFactory workProcessFactory;
    private final DeconstructBuildingModuleService deconstructBuildingModuleService;

    void initiateDeconstructModules(Game game, UUID deconstructionId) {
        GameData gameData = game.getData();

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionIdValidated(deconstructionId);

        UUID constructionAreaId = deconstruction.getExternalReference();

        gameData.getBuildingModules()
            .getByConstructionAreaId(constructionAreaId)
            .forEach(buildingModule -> deconstructBuildingModule(game, buildingModule));
    }

    private void deconstructBuildingModule(Game game, BuildingModule buildingModule) {
        if (game.getData().getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()).isPresent()) {
            log.info("BuildingModule {} is already being deconstructed.", buildingModule.getBuildingModuleId());
            return;
        }

        log.info("Deconstructing BuildingModule {}", buildingModule.getBuildingModuleId());
        deconstructBuildingModuleService.deconstructBuildingModule(game, buildingModule.getLocation(), buildingModule.getBuildingModuleId());
    }


    void startWork(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID deconstructionId, UUID location) {
        workProcessFactory.createForDeconstruction(gameData, processId, deconstructionId, location)
            .forEach(workProcess -> {
                gameData.getProcesses()
                    .add(workProcess);
                progressDiff.save(workProcess.toModel());
            });
    }

    public void finishDeconstruction(GameProgressDiff progressDiff, GameData gameData, UUID deconstructionId) {
        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionIdValidated(deconstructionId);

        UUID constructionAreaId = deconstruction.getExternalReference();
        ConstructionArea constructionArea = gameData.getConstructionAreas()
            .findByConstructionAreaIdValidated(constructionAreaId);

        gameData.getConstructionAreas()
            .remove(constructionArea);
        progressDiff.delete(constructionAreaId, GameItemType.CONSTRUCTION_AREA);

        gameData.getDeconstructions()
            .remove(deconstruction);
        progressDiff.delete(deconstructionId, GameItemType.DECONSTRUCTION);
    }
}
