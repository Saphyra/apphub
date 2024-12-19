package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
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
}
