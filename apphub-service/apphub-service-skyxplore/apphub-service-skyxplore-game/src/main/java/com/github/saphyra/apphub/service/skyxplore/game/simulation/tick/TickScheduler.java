package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.concurrent.Future;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@Builder
class TickScheduler implements Runnable {
    private final Game game;
    private final TickSchedulerContext context;

    @SneakyThrows
    @Override
    public void run() {
        long sleepTime = 0;

        while (!game.isTerminated()) {
            while (game.isGamePaused()) {
                context.getSleepService()
                    .sleep(100);

                if (game.isTerminated()) {
                    break;
                }
            }

            if (game.isTerminated()) {
                break;
            }

            log.info("TickScheduler started for game {}", game.getGameId());
            context.getSleepService()
                .sleep(sleepTime);

            long startTime = context.getDateTimeUtil()
                .getCurrentTimeEpochMillis();

            game.getEventLoop()
                .process(
                    () -> context.getTickTasks()
                        .stream()
                        .sorted(Comparator.comparingInt(value -> value.getOrder().getOrder()))
                        .forEach(tickTask -> tickTask.process(game)));

            Future<ExecutionResult<Long>> future = game.getEventLoop()
                .processWithResponse(() -> {
                    long endTime = context.getDateTimeUtil()
                        .getCurrentTimeEpochMillis();
                    long processingTime = endTime - startTime;
                    log.info("Tick finished for game {} in {} ms", game.getGameId(), processingTime);
                    return processingTime;
                });

            long processingTime = future.get()
                .getOrThrow();

            sleepTime = Math.max(0, context.getGameProperties().getTickTimeMillis() - (processingTime));
            log.info("Next tick for game {} is will be started in {}ms.", game.getGameId(), sleepTime);
        }

        log.info("TickScheduler finished for game {}", game.getGameId());
    }
}
