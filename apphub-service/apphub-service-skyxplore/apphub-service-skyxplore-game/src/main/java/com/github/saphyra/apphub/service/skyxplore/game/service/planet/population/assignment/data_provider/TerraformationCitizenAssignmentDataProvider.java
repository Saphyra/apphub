package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.data_provider;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
class TerraformationCitizenAssignmentDataProvider implements CitizenAssignmentDataProvider {
    @Override
    public ProcessType getType() {
        return ProcessType.TERRAFORMATION;
    }

    @Override
    public Object getData(GameData gameData, Process process) {
        Construction terraformation = gameData.getConstructions()
            .findByIdValidated(process.getExternalReference());
        SurfaceType originalSurfaceType = gameData.getSurfaces()
            .findByIdValidated(terraformation.getExternalReference())
            .getSurfaceType();
        return Map.of(
            "originalSurfaceType", originalSurfaceType,
            "targetSurfaceType", terraformation.getData()
        );
    }
}
