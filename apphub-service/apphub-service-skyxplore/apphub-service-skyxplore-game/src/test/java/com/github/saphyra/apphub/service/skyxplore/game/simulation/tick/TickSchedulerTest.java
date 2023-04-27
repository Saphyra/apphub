package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TickSchedulerTest {
    private static final Long START_TIME_1 = 1L;
    private static final Long START_TIME_2 = 10L;
    private static final Long END_TIME_1 = 5L;
    private static final Long END_TIME_2 = 50L;
    private static final Integer TICK_TIME_MILLIS = 30;

    @Mock
    private Game game;

    @Mock
    private TickSchedulerContext context;

    @Mock
    private SleepService sleepServiceMock = new SleepService();

    private final SleepService sleepService = new SleepService();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private TickScheduler underTest;

    @Mock
    private SyncCache syncCache;

    private EventLoop eventLoop;

    @Mock
    private TickTask tickTask;

    @BeforeEach
    void setUp() {
        eventLoop = new EventLoop(new ExecutorServiceBeanFactory(sleepService, errorReporterService));
    }

    @Test
    void run_exitWhenPausedGameIsTerminated() {
        given(context.getSleepService()).willReturn(sleepServiceMock);

        given(game.isTerminated())
            .willReturn(false)
            .willReturn(false)
            .willReturn(true);
        given(game.isGamePaused()).willReturn(true);

        underTest.run();

        verify(sleepServiceMock, times(2)).sleep(100);
    }

    @Test
    void run_schedule() throws Exception {
        given(context.getSleepService()).willReturn(sleepService);
        given(context.getDateTimeUtil()).willReturn(dateTimeUtil);
        given(context.getSyncCacheFactory()).willReturn(syncCacheFactory);
        given(context.getTickTasks()).willReturn(List.of(tickTask));
        given(context.getGameProperties()).willReturn(gameProperties);
        given(gameProperties.getTickTimeMillis()).willReturn(TICK_TIME_MILLIS);

        given(game.isGamePaused()).willReturn(false);
        given(game.isTerminated())
            .willReturn(false)
            .willReturn(false)
            .willReturn(false)
            .willReturn(false)
            .willReturn(true);
        given(game.getEventLoop()).willReturn(eventLoop);

        given(dateTimeUtil.getCurrentTimeEpochMillis())
            .willReturn(START_TIME_1)
            .willReturn(END_TIME_1)
            .willReturn(START_TIME_2)
            .willReturn(END_TIME_2);

        given(syncCacheFactory.create(game)).willReturn(syncCache);

        underTest.run();

        sleepService.sleep(1000);

        verify(tickTask, times(2)).process(game, syncCache);
    }
}