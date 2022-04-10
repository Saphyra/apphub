package com.github.saphyra.apphub.service.skyxplore.game.process.background.satiety;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.service.GameSleepService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SatietyDecreaseProcessTest {
    @Mock
    private Game game;

    @Mock
    private ProcessContext processContext;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private SleepService sleepService;

    @Mock
    private SatietyDecreaseService satietyDecreaseService;

    @Mock
    private GameSleepService gameSleepService;

    @InjectMocks
    private SatietyDecreaseProcess underTest;

    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(Mockito.mock(ErrorReporterService.class));

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Future<ExecutionResult<Void>> future;

    @Mock
    private SyncCache syncCache;

    @Test
    public void triggerSatietyDecrease() {
        given(game.isTerminated()).willReturn(false)
            .willReturn(true);

        given(processContext.getExecutorServiceBean()).willReturn(executorServiceBean);
        given(processContext.getSleepService()).willReturn(sleepService);
        given(processContext.getSyncCacheFactory()).willReturn(syncCacheFactory);
        given(processContext.getSatietyDecreaseService()).willReturn(satietyDecreaseService);
        given(processContext.getGameSleepService()).willReturn(gameSleepService);

        given(syncCacheFactory.create()).willReturn(syncCache);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.process(any(), eq(syncCache))).willReturn(future);

        given(future.isDone()).willReturn(false)
            .willReturn(true);

        underTest.startProcess();

        verify(sleepService, timeout(1000)).sleep(100);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).process(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(satietyDecreaseService).processGame(game, syncCache);
        verify(gameSleepService).sleepASecond(game);
    }
}