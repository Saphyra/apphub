package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenAssignmentResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CitizenAssignmentProviderTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private CitizenAssignmentQueryService citizenAssignmentQueryService;

    @InjectMocks
    private CitizenAssignmentProvider underTest;

    @Mock
    private GameData gameData;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private CitizenAssignmentResponse citizenAssignmentResponse;

    @Test
    void getAssignment() {
        given(gameData.getCitizenAllocations()).willReturn(CollectionUtils.toList(new CitizenAllocations(), citizenAllocation));
        given(citizenAllocation.getCitizenId()).willReturn(CITIZEN_ID);
        given(gameData.getProcesses()).willReturn(processes);
        given(citizenAllocation.getProcessId()).willReturn(PROCESS_ID);
        given(processes.findByIdValidated(PROCESS_ID)).willReturn(process);
        given(citizenAssignmentQueryService.getAssignment(gameData, process)).willReturn(citizenAssignmentResponse);

        CitizenAssignmentResponse result = underTest.getAssignment(gameData, CITIZEN_ID);

        assertThat(result).isEqualTo(citizenAssignmentResponse);
    }

    @Test
    void getAssignment_unassigned() {
        given(gameData.getCitizenAllocations()).willReturn(new CitizenAllocations());

        CitizenAssignmentResponse result = underTest.getAssignment(gameData, CITIZEN_ID);

        assertThat(result.getType()).isEqualTo(GameConstants.CITIZEN_ASSIGNMENT_TYPE_IDLE);
    }
}