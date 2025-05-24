package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

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
//TODO unit test
class WorkProcessConditions {
    boolean hasBuildingAllocated(GameData gameData, UUID processId) {
        return gameData.getBuildingModuleAllocations()
            .findByProcessId(processId)
            .isPresent();
    }

    boolean hasCitizenAllocated(GameData gameData, UUID processId) {
        return gameData.getCitizenAllocations()
            .findByProcessId(processId)
            .isPresent();
    }

    /**
     * WorkProcess can proceed if the same parent has no other workProcesses in progress
     */
    public boolean canProceed(GameData gameData, UUID externalReference) {
        return gameData.getProcesses()
            .getByExternalReferenceAndType(externalReference, ProcessType.WORK)
            .stream()
            .noneMatch(process -> process.getStatus() == ProcessStatus.IN_PROGRESS);
    }
}
