package com.github.saphyra.apphub.service.skyxplore.game.tick;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TickSchedulerService {
    private final ProcessTickService processTickService;
    private final ScheduledExecutorServiceBean scheduler;
    private final int delaySeconds;

    @Builder
    public TickSchedulerService(
        ProcessTickService processTickService,
        ExecutorServiceBeanFactory executorServiceBeanFactory,
        @Value("${game.tick.schedulerThreadCount}") int schedulerThreadCount,
        @Value("${game.tick.delaySeconds}") int delaySeconds
    ) {
        this.processTickService = processTickService;
        scheduler = executorServiceBeanFactory.createScheduled(schedulerThreadCount);
        this.delaySeconds = delaySeconds;
    }

    public void addGame(Game game) {
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleWithFixedDelay(() -> processTickService.processTick(game), delaySeconds, delaySeconds, TimeUnit.SECONDS);
    }
}
