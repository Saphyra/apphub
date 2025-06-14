package com.github.saphyra.apphub.service.skyxplore.game.util;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

//TODO unit test
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkPointsUtil {
    public Integer getCompletedWorkPoints(GameData gameData, UUID constructionId, ProcessType processType) {
        return gameData.getProcesses()
            .findByExternalReferenceAndType(constructionId, processType)
            .map(process -> gameData.getProcesses().getByExternalReferenceAndType(process.getProcessId(), ProcessType.WORK))
            .stream()
            .flatMap(Collection::stream)
            .map(process -> (WorkProcess) process)
            .mapToInt(WorkProcess::getCompletedWorkPoints)
            .sum();
    }
}
