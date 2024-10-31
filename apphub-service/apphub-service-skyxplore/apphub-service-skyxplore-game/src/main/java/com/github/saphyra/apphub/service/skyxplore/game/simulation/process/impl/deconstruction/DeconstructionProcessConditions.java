package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructionProcessConditions {
    boolean buildingUtilized(GameData gameData, UUID deconstructionId) {
        UUID buildingId = gameData.getDeconstructions()
            .findByDeconstructionIdValidated(deconstructionId)
            .getExternalReference();

        return !gameData.getBuildingAllocations()
            .getByBuildingModuleId(buildingId)
            .isEmpty();
    }

    boolean workFinished(GameData gameData, UUID processId) {
        return gameData.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.WORK)
            .stream()
            .allMatch(process -> process.getStatus() == ProcessStatus.DONE);
    }
}
