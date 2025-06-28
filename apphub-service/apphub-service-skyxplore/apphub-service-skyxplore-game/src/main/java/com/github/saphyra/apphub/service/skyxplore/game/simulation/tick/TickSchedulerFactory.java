package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TickSchedulerFactory {
    private final TickSchedulerContext context;

    TickScheduler getTickScheduler(Game game) {
        return TickScheduler.builder()
            .game(game)
            .context(context)
            .build();
    }
}
