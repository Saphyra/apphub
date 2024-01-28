package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenAssignmentResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CitizenAssignmentProvider {
    private final CitizenAssignmentQueryService citizenAssignmentQueryService;

    public CitizenAssignmentResponse getAssignment(GameData gameData, UUID citizenId) {
        return gameData.getCitizenAllocations()
            .stream()
            .filter(citizenAllocation -> citizenAllocation.getCitizenId().equals(citizenId))
            .findAny()
            .map(citizenAllocation -> getAssignmentResponse(gameData, citizenAllocation.getProcessId()))
            .orElse(CitizenAssignmentResponse.builder()
                .type(GameConstants.CITIZEN_ASSIGNMENT_TYPE_IDLE)
                .build());
    }

    private CitizenAssignmentResponse getAssignmentResponse(GameData gameData, UUID processId) {
        Process process = gameData.getProcesses()
            .findByIdValidated(processId);
        return citizenAssignmentQueryService.getAssignment(gameData, process);
    }
}
