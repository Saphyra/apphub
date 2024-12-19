package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.data_provider;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructBuildingModuleCitizenAssignmentDataProvider implements CitizenAssignmentDataProvider {
    @Override
    public ProcessType getType() {
        return ProcessType.CONSTRUCT_BUILDING_MODULE;
    }

    @Override
    public Object getData(GameData gameData, Process process) {
        UUID buildingModuleId = gameData.getConstructions()
            .findByConstructionIdValidated(process.getExternalReference())
            .getExternalReference();
        return gameData.getBuildingModules()
            .findByBuildingModuleIdValidated(buildingModuleId)
            .getDataId();
    }
}
