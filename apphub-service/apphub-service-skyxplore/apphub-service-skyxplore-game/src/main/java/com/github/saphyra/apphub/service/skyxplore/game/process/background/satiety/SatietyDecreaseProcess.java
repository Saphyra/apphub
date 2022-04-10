package com.github.saphyra.apphub.service.skyxplore.game.process.background.satiety;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class SatietyDecreaseProcess {
    private final Game game;
    private final ProcessContext processContext;

    public SatietyDecreaseProcess startProcess() {
        processContext.getExecutorServiceBean()
            .execute(() -> {
                log.info("Starting SatietyDecreaseProcess for game {}", game.getGameId());

                while (!game.isTerminated()) {
                    processContext.getGameSleepService()
                        .sleepASecond(game);

                    SyncCache syncCache = processContext.getSyncCacheFactory()
                        .create();

                    Future<?> future = game.getEventLoop()
                        .process(() -> processContext.getSatietyDecreaseService().processGame(game, syncCache), syncCache);

                    while (!future.isDone()) {
                        processContext.getSleepService()
                            .sleep(100);
                    }
                }

                log.info("Stopping SatietyDecreaseProcess for game {}", game.getGameId());
            });

        return this;
    }
}
