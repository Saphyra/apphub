package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TickSchedulerLauncher {
    private final ExecutorServiceBean executorServiceBean;
    private final TickSchedulerContext context;

    public void launch(Game game) {
        TickScheduler tickScheduler = TickScheduler.builder()
            .game(game)
            .context(context)
            .build();

        executorServiceBean.execute(tickScheduler);
    }
}
