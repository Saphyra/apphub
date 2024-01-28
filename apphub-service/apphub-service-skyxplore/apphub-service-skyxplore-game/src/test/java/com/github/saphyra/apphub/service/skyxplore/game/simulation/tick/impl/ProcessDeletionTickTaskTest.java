package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessDeletionTickTaskTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();

    private final ProcessDeletionTickTask underTest = new ProcessDeletionTickTask();

    @Mock
    private Game game;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private Process readyToDeleteProcess;

    @Mock
    private Process otherProcess;

    @Test
    void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(TickTaskOrder.PROCESS_DELETION);
    }

    @Test
    void process() {
        given(game.getData()).willReturn(gameData);
        Processes processes = CollectionUtils.toList(new Processes(), readyToDeleteProcess, otherProcess);
        given(gameData.getProcesses()).willReturn(processes);
        given(otherProcess.getStatus()).willReturn(ProcessStatus.DONE);
        given(readyToDeleteProcess.getStatus()).willReturn(ProcessStatus.READY_TO_DELETE);
        given(readyToDeleteProcess.getProcessId()).willReturn(PROCESS_ID);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.process(game);

        verify(readyToDeleteProcess).cleanup();
        verify(progressDiff).delete(PROCESS_ID, GameItemType.PROCESS);
        assertThat(processes).containsExactly(otherProcess);
    }
}