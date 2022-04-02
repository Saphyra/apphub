package com.github.saphyra.apphub.service.skyxplore.game.process.background;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
//TODO unit test
public class FinishedProcessCleanupProcess {
    public FinishedProcessCleanupProcess(Game game, ProcessContext processContext) {
        startProcess(game, processContext);
    }

    private void startProcess(Game game, ProcessContext processContext) {
        processContext.getExecutorServiceBean()
            .execute(() -> {
                log.info("Starting ProcessSchedulerProcess for game {}", game.getGameId());

                while (!game.isTerminated()) {
                    Future<?> future = game.getEventLoop()
                        .process(() -> processGame(game, processContext.getExecutorServiceBean(), processContext.getGameDataProxy()));

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

    private void processGame(Game game, ExecutorServiceBean executorServiceBean, GameDataProxy gameDataProxy) {
        SyncCache syncCache = new SyncCache();

        Processes processes = game.getProcesses();
        synchronized (processes) {
            List<Process> finishedProcesses = processes
                .stream()
                .filter(process -> process.getStatus() == ProcessStatus.READY_TO_DELETE)
                .collect(Collectors.toList());

            finishedProcesses.forEach(process -> syncCache.deleteGameItem(process.getProcessId(), GameItemType.PROCESS));
            syncCache.process(executorServiceBean, gameDataProxy);

            processes.removeAll(finishedProcesses);
        }
    }
}
