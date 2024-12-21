package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeconstructConstructionAreaProcessConditionsTest {
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @InjectMocks
    private DeconstructConstructionAreaProcessConditions underTest;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Test
    void modulesDeconstructed() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));

        assertThat(underTest.modulesDeconstructed(gameData, CONSTRUCTION_AREA_ID)).isFalse();
    }


    @Test
    void hasWorkProcesses() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.WORK)).willReturn(List.of(process));

        assertThat(underTest.hasWorkProcesses(gameData, PROCESS_ID)).isTrue();
    }

    @Test
    void workFinished() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.WORK)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);

        assertThat(underTest.workFinished(gameData, PROCESS_ID)).isTrue();
    }
}