package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area;

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
class DeconstructConstructionAreaProcessConditions {
    boolean modulesDeconstructed(GameData gameData, UUID constructionAreaId) {
        return gameData.getBuildingModules()
            .getByConstructionAreaId(constructionAreaId)
            .isEmpty();
    }

    boolean hasWorkProcess(GameData gameData, UUID processId) {
        return !gameData.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.WORK)
            .isEmpty();
    }

    public boolean workFinished(GameData gameData, UUID processId) {
        return gameData.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.WORK)
            .stream()
            .allMatch(process -> process.getStatus() == ProcessStatus.DONE);
    }
}
