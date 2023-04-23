package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductionOrderProcessConditionsTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();

    private final ProductionOrderProcessConditions underTest = new ProductionOrderProcessConditions();

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private GameData gameData;

    @Test
    void requiredResourcesPresent() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.PRODUCTION_ORDER)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);

        assertThat(underTest.requiredResourcesPresent(gameData, PROCESS_ID)).isTrue();
    }

    @Test
    void workStarted() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.WORK)).willReturn(List.of(process));

        assertThat(underTest.workStarted(gameData, PROCESS_ID)).isTrue();
    }

    @Test
    void workDone() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.WORK)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);

        assertThat(underTest.workDone(gameData, PROCESS_ID)).isTrue();
    }
}