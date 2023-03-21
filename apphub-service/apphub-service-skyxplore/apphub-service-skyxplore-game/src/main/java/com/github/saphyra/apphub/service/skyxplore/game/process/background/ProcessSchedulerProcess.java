package com.github.saphyra.apphub.service.skyxplore.game.process.background;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class ProcessSchedulerProcess {
    private final Game game;
    private final ProcessContext processContext;

    public void startProcess() {
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
                        .sleep(game, 500);
                }

                log.info("Stopping ProcessSchedulerProcess for game {}", game.getGameId());
            });
    }

    private void processGame(Game game) {
        Processes processes = game.getProcesses();
        synchronized (processes) {
            processes.stream()
                .filter(process -> process.getStatus() == ProcessStatus.IN_PROGRESS || process.getStatus() == ProcessStatus.CREATED)
                .sorted(Process::compareTo)
                .forEach(process -> scheduleProcess(game.getEventLoop(), process));
        }
    }

    private void scheduleProcess(EventLoop eventLoop, Process process) {
        log.info("Scheduling process {}", process);
        SyncCache syncCache = processContext.getSyncCacheFactory()
            .create();
        eventLoop.process(() -> process.scheduleWork(syncCache), syncCache);
    }

}
