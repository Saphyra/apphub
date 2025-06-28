package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TickSchedulerLauncher {
    private final ExecutorServiceBean executorServiceBean;
    private final TickSchedulerFactory tickSchedulerFactory;
    private final GameDao gameDao;

    public void launch(Game game) {
        TickScheduler tickScheduler = tickSchedulerFactory.getTickScheduler(game);

        executorServiceBean.execute(tickScheduler);
    }

    @SneakyThrows
    public void processTick(UUID userId) {
        Game game = gameDao.findByUserIdValidated(userId);
        TickScheduler tickScheduler = tickSchedulerFactory.getTickScheduler(game);

        synchronized (game) {
            executorServiceBean.execute(() -> tickScheduler.processTick(0L))
                .get()
                .getOrThrow();
        }
    }
}
