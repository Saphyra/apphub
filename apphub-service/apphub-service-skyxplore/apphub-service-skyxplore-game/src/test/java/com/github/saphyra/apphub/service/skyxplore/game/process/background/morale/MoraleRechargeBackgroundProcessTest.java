package com.github.saphyra.apphub.service.skyxplore.game.process.background.morale;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.service.GameSleepService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MoraleRechargeBackgroundProcessTest {
    @Mock
    private Game game;

    @Mock
    private ProcessContext processContext;

    @InjectMocks
    private MoraleRechargeBackgroundProcess underTest;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private GameSleepService gameSleepService;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Future<ExecutionResult<Void>> future;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private SleepService sleepService;

    @Mock
    private PassiveMoraleRechargeService passiveMoraleRechargeService;

    @Mock
    private ActiveMoraleRechargeService activeMoraleRechargeService;

    @Test
    void startProcess() {
        given(processContext.getExecutorServiceBean()).willReturn(executorServiceBean);
        given(processContext.getGameSleepService()).willReturn(gameSleepService);
        given(processContext.getSyncCacheFactory()).willReturn(syncCacheFactory);
        given(processContext.getSleepService()).willReturn(sleepService);
        given(processContext.getPassiveMoraleRechargeService()).willReturn(passiveMoraleRechargeService);
        given(processContext.getActiveMoraleRechargeService()).willReturn(activeMoraleRechargeService);

        given(syncCacheFactory.create()).willReturn(syncCache);

        given(game.isTerminated())
            .willReturn(false)
            .willReturn(true);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.process(any(), eq(syncCache))).willReturn(future);
        given(future.isDone())
            .willReturn(false)
            .willReturn(true);

        underTest.startProcess();

        ArgumentCaptor<Runnable> mainArgumentCapture = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceBean).execute(mainArgumentCapture.capture());
        mainArgumentCapture.getValue()
            .run();

        ArgumentCaptor<Runnable> eventLoopArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).process(eventLoopArgumentCaptor.capture(), eq(syncCache));
        eventLoopArgumentCaptor.getValue()
            .run();

        verify(gameSleepService).sleepASecond(game);
        verify(sleepService).sleep(100);
        verify(passiveMoraleRechargeService).processGame(game, syncCache);
        verify(activeMoraleRechargeService).processGame(game, syncCache);
    }
}