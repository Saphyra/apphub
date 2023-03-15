package com.github.saphyra.apphub.service.skyxplore.game.process.background;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class FinishedProcessCleanupProcess {
    private final Game game;
    private final ProcessContext processContext;

    public void startProcess() {
        processContext.getExecutorServiceBean()
            .execute(() -> {
                log.info("Starting FinishedProcessCleanupProcess for game {}", game.getGameId());

                while (!game.isTerminated()) {
                    SyncCache syncCache = processContext.getSyncCacheFactory()
                        .create();
                    Future<?> future = game.getEventLoop()
                        .process(() -> processGame(syncCache, game), syncCache);

                    while (!future.isDone()) {
                        processContext.getSleepService()
                            .sleep(100);
                    }

                    processContext.getSleepService()
                        .sleep(1000);
                }

                log.info("Stopping ProcessSchedulerProcess for game {}", game.getGameId());
            });

    }

    private void processGame(SyncCache syncCache, Game game) {
        log.debug("Cleaning up finished processes...");

        Processes processes = game.getProcesses();
        List<Process> finishedProcesses = game.getProcesses()
            .stream()
            .filter(process -> process.getStatus() == ProcessStatus.READY_TO_DELETE)
            .collect(Collectors.toList());

        log.debug("Deleting processes {}", finishedProcesses);

        finishedProcesses.forEach(process -> syncCache.deleteGameItem(process.getProcessId(), GameItemType.PROCESS));
        syncCache.process();

        processes.removeAll(finishedProcesses);
    }
}
