package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module;

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
class ConstructBuildingModuleProcessConditions {
    boolean productionOrdersComplete(GameData gameData, UUID processId) {
        return gameData.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.PRODUCTION_ORDER)
            .stream()
            .allMatch(process -> process.getStatus() == ProcessStatus.DONE);
    }

    boolean hasWorkProcesses(GameData gameData, UUID processId) {
        return !gameData.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.WORK)
            .isEmpty();
    }

    boolean workFinished(GameData gameData, UUID processId) {
        return gameData.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.WORK)
            .stream()
            .allMatch(process -> process.getStatus() == ProcessStatus.DONE);
    }
}
