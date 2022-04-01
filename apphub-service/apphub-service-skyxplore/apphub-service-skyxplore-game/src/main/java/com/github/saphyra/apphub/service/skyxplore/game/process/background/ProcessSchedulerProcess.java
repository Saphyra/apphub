package com.github.saphyra.apphub.service.skyxplore.game.process.background;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

@Slf4j
//TODO unit test
public class ProcessSchedulerProcess {
    public ProcessSchedulerProcess(Game game, ProcessContext processContext) {
        startProcess(game, processContext);
    }

    private void startProcess(Game game, ProcessContext processContext) {
        processContext.getExecutorServiceBean()
            .execute(() -> {
                log.info("Starting ProcessSchedulerProcess for game {}", game.getGameId());

                while (!game.isTerminated()) {
                    Future<?> future = game.getEventLoop()
                        .process(() -> processGame(game));

                    while (!future.isDone()) {
                        processContext.getSleepService()
                            .sleep(100);
                    }

                    processContext.getGameSleepService()
                        .sleepASecond(game);
                }

                log.info("Stopping ProcessSchedulerProcess for game {}", game.getGameId());
            });
    }

    private void processGame(Game game) {
        game.getProcesses()
            .stream()
            .sorted(Process::compareTo)
            .forEach(process -> scheduleProcess(game.getEventLoop(), process));
    }

    private void scheduleProcess(EventLoop eventLoop, Process process) {
        SyncCache syncCache = new SyncCache();
        eventLoop.process(() -> process.scheduleWork(syncCache), syncCache);
    }

}
