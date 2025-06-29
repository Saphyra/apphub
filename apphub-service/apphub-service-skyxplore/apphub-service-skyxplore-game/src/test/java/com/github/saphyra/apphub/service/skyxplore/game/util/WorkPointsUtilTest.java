package com.github.saphyra.apphub.service.skyxplore.game.util;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WorkPointsUtilTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final int COMPLETED_WORK_POINTS = 23;
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @InjectMocks
    private WorkPointsUtil underTest;

    @Mock
    private GameData gameData;

    @Mock
    private WorkProcess workProcess;

    @Mock
    private Process process;

    @Mock
    private Processes processes;

    @Test
    void getCompletedWorkPoints() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndType(EXTERNAL_REFERENCE, ProcessType.CONSTRUCT_CONSTRUCTION_AREA)).willReturn(Optional.of(process));
        given(process.getProcessId()).willReturn(PROCESS_ID);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.WORK)).willReturn(List.of(workProcess, workProcess));
        given(workProcess.getCompletedWorkPoints()).willReturn(COMPLETED_WORK_POINTS);

        assertThat(underTest.getCompletedWorkPoints(gameData, EXTERNAL_REFERENCE, ProcessType.CONSTRUCT_CONSTRUCTION_AREA)).isEqualTo(COMPLETED_WORK_POINTS * 2);
    }
}