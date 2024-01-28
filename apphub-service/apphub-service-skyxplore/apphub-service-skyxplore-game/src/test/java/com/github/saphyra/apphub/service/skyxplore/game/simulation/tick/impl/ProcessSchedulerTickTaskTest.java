package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessSchedulerTickTaskTest {
    private final ProcessSchedulerTickTask underTest = new ProcessSchedulerTickTask();

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Process finishedProcess;

    @Mock
    private Process highPriorityProcess;

    @Mock
    private Process lowPriorityProcess;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private EventLoop eventLoop;

    @Test
    void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(TickTaskOrder.PROCESS_SCHEDULER);
    }

    @Test
    void process() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(CollectionUtils.toList(new Processes(), finishedProcess, highPriorityProcess, lowPriorityProcess));
        given(finishedProcess.getStatus()).willReturn(ProcessStatus.DONE);
        given(lowPriorityProcess.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(highPriorityProcess.getStatus()).willReturn(ProcessStatus.CREATED);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.process(game);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop, times(2)).process(argumentCaptor.capture());

        argumentCaptor.getAllValues()
            .get(0)
            .run();

        verify(highPriorityProcess).scheduleWork(progressDiff);
        verify(lowPriorityProcess, times(0)).scheduleWork(progressDiff);

        argumentCaptor.getAllValues()
            .get(1)
            .run();

        verify(lowPriorityProcess).scheduleWork(progressDiff);
    }
}