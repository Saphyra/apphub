package com.github.saphyra.apphub.service.skyxplore.game.tick;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TickSchedulerServiceTest {
    @Mock
    private ProcessTickService processTickService;

    @Mock
    private ErrorReporterService errorReporterService;

    private TickSchedulerService underTest;

    @Mock
    private Game game;

    @Before
    public void setUp() {
        underTest = TickSchedulerService.builder()
            .processTickService(processTickService)
            .schedulerThreadCount(1)
            .delaySeconds(1)
            .executorServiceBeanFactory(ExecutorServiceBeenTestUtils.createFactory(errorReporterService))
            .build();
    }

    @Test
    public void addGame() throws InterruptedException {
        underTest.addGame(game);

        Thread.sleep(3000);

        verify(processTickService, atLeast(2)).processTick(game);
    }
}