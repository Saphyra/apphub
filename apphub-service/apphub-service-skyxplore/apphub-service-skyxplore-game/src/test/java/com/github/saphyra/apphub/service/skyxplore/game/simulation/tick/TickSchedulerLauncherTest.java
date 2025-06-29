package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TickSchedulerLauncherTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private TickSchedulerFactory tickSchedulerFactory;

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private TickSchedulerLauncher underTest;

    @Mock
    private Game game;

    @Mock
    private TickScheduler tickScheduler;

    @Mock
    private Future<ExecutionResult<Void>> future;

    @Mock
    private ExecutionResult<Void> executionResult;

    @BeforeEach
    void setUp() {
        given(tickSchedulerFactory.getTickScheduler(game)).willReturn(tickScheduler);
    }

    @Test
    void launch() {
        underTest.launch(game);

        then(executorServiceBean).should().execute(tickScheduler);
    }

    @Test
    void processTick() throws ExecutionException, InterruptedException {
        given(executorServiceBean.execute(any(Runnable.class))).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return future;
        });
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(future.get()).willReturn(executionResult);

        underTest.processTick(USER_ID);

        then(tickScheduler).should().processTick(0L);
    }
}