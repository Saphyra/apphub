package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
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
