package com.github.saphyra.apphub.service.skyxplore.game.process.background;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FinishedProcessCleanupProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private Game game;

    @Mock
    private ProcessContext processContext;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private SleepService sleepService;

    @InjectMocks
    private FinishedProcessCleanupProcess underTest;

    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(Mockito.mock(ErrorReporterService.class));
    private final Processes processes = new Processes();

    @Mock
    private Process process;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Future<ExecutionResult<Void>> future;

    @Mock
    private SyncCache syncCache;

    @Test
    public void cleanUpFinishedProcesses() {
        given(game.isTerminated()).willReturn(false)
            .willReturn(true);

        given(processContext.getExecutorServiceBean()).willReturn(executorServiceBean);
        given(processContext.getSleepService()).willReturn(sleepService);
        given(processContext.getSyncCacheFactory()).willReturn(syncCacheFactory);

        given(syncCacheFactory.create()).willReturn(syncCache);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.process(any(), eq(syncCache))).willReturn(future);

        given(future.isDone()).willReturn(false)
            .willReturn(true);

        given(game.getProcesses()).willReturn(processes);

        given(process.getStatus()).willReturn(ProcessStatus.READY_TO_DELETE);
        given(process.getProcessId()).willReturn(PROCESS_ID);

        processes.add(process);

        underTest.startProcess();

        verify(sleepService, timeout(1000)).sleep(100);
        verify(sleepService, timeout(2000)).sleep(1000);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).process(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(syncCache).deleteGameItem(PROCESS_ID, GameItemType.PROCESS);
        verify(syncCache).process();
    }
}