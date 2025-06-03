package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
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
    private final GameProperties gameProperties;

    void initiateDeconstructModules(Game game, UUID deconstructionId) {
        GameData gameData = game.getData();

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByIdValidated(deconstructionId);

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

    //TODO unit test
    void startWork(Game game, UUID processId, UUID deconstructionId) {
        Deconstruction deconstruction = game.getData()
            .getDeconstructions()
            .findByIdValidated(deconstructionId);

        workProcessFactory.save(game, deconstruction.getLocation(), processId, gameProperties.getDeconstruction().getRequiredWorkPoints(), SkillType.BUILDING);
    }

    void finishDeconstruction(GameProgressDiff progressDiff, GameData gameData, UUID deconstructionId) {
        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByIdValidated(deconstructionId);

        UUID constructionAreaId = deconstruction.getExternalReference();
        ConstructionArea constructionArea = gameData.getConstructionAreas()
            .findByIdValidated(constructionAreaId);

        gameData.getConstructionAreas()
            .remove(constructionArea);
        progressDiff.delete(constructionAreaId, GameItemType.CONSTRUCTION_AREA);

        gameData.getDeconstructions()
            .remove(deconstruction);
        progressDiff.delete(deconstructionId, GameItemType.DECONSTRUCTION);
    }
}
