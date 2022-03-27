package com.github.saphyra.apphub.service.skyxplore.game.domain.process;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
//TODO unit test
public class EventLoop {
    private final ExecutorServiceBean eventLoopThread;
    private final ExecutorServiceBean generalExecutor;
    private final GameDataProxy gameDataProxy;

    @Builder
    public EventLoop(ExecutorServiceBeanFactory executorServiceBeanFactory, ExecutorServiceBean generalExecutor, GameDataProxy gameDataProxy) {
        this.eventLoopThread = executorServiceBeanFactory.create(Executors.newSingleThreadExecutor());
        this.generalExecutor = generalExecutor;
        this.gameDataProxy = gameDataProxy;
    }

    public Future<?> process(Runnable runnable, MessageCache messageCache, GameItemCache gameItemCache) {
        return eventLoopThread.execute(() -> {
            runnable.run();

            messageCache.process(generalExecutor);
            gameItemCache.process(gameDataProxy);
        });
    }

    public void stop() {
        eventLoopThread.stop();
    }
}
