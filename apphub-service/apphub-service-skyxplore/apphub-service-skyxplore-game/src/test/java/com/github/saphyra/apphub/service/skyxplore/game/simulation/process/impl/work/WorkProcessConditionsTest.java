package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WorkProcessConditionsTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    private final WorkProcessConditions underTest = new WorkProcessConditions();

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModuleAllocations buildingModuleAllocations;

    @Mock
    private BuildingModuleAllocation buildingModuleAllocation;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Test
    void hasBuildingAllocated() {
        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);
        given(buildingModuleAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(buildingModuleAllocation));

        assertThat(underTest.hasBuildingAllocated(gameData, PROCESS_ID)).isTrue();
    }

    @Test
    void hasCitizenAllocated() {
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(citizenAllocation));

        assertThat(underTest.hasCitizenAllocated(gameData, PROCESS_ID)).isTrue();
    }

    @Test
    void canProceed() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(EXTERNAL_REFERENCE, ProcessType.WORK)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);

        assertThat(underTest.canProceed(gameData, EXTERNAL_REFERENCE)).isTrue();
    }
}