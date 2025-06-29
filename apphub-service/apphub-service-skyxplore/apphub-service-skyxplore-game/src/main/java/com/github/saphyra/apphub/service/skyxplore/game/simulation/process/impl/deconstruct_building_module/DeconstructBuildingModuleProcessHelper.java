package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructBuildingModuleProcessHelper {
    private final WorkProcessFactory workProcessFactory;
    private final GameProperties gameProperties;

    void startWork(Game game, UUID processId, UUID deconstructionId) {
        Deconstruction deconstruction = game.getData()
            .getDeconstructions()
            .findByIdValidated(deconstructionId);

        int requiredWorkPoints = gameProperties.getDeconstruction()
            .getRequiredWorkPoints();

        workProcessFactory.save(game, deconstruction.getLocation(), processId, requiredWorkPoints, SkillType.BUILDING);
    }

    void finishDeconstruction(GameProgressDiff progressDiff, GameData gameData, UUID deconstructionId) {
        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByIdValidated(deconstructionId);

        UUID buildingModuleId = deconstruction.getExternalReference();
        BuildingModule buildingModule = gameData.getBuildingModules()
            .findByIdValidated(buildingModuleId);

        gameData.getBuildingModules()
            .remove(buildingModule);
        progressDiff.delete(buildingModuleId, GameItemType.BUILDING_MODULE);

        gameData.getDeconstructions()
            .remove(deconstruction);
        progressDiff.delete(deconstructionId, GameItemType.DECONSTRUCTION);
    }
}
