package com.github.saphyra.apphub.service.skyxplore.game.process.background.morale;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class PassiveMoraleRechargeBackgroundProcess {
    private final Game game;
    private final ProcessContext processContext;

    public void startProcess() {
        processContext.getExecutorServiceBean()
            .execute(() -> {
                log.info("Starting PassiveMoraleRechargeBackgroundProcess for game {}", game.getGameId());

                while (!game.isTerminated()) {
                    processContext.getGameSleepService()
                        .sleepASecond(game);

                    SyncCache syncCache = processContext.getSyncCacheFactory()
                        .create();

                    Future<?> future = game.getEventLoop()
                        .process(() -> processContext.getPassiveMoraleRechargeService().processGame(game, syncCache), syncCache);

                    while (!future.isDone()) {
                        processContext.getSleepService()
                            .sleep(100);
                    }
                }

                log.info("Stopping PassiveMoraleRechargeBackgroundProcess for game {}", game.getGameId());
            });
    }
}
