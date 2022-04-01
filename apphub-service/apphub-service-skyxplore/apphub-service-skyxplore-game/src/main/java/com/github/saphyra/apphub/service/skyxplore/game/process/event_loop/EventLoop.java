package com.github.saphyra.apphub.service.skyxplore.game.process.event_loop;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
//TODO unit test
public class EventLoop {
    private final ExecutorServiceBean eventLoopThread;
    private final ExecutorServiceBean generalExecutor;
    private final GameDataProxy gameDataProxy;
    private final Collection<Runnable> queue;

    @Builder
    public EventLoop(ExecutorServiceBeanFactory executorServiceBeanFactory, ExecutorServiceBean generalExecutor, GameDataProxy gameDataProxy) {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        this.queue = queue;
        this.eventLoopThread = executorServiceBeanFactory.create(
            new ThreadPoolExecutor(1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                queue
            )
        );
        this.generalExecutor = generalExecutor;
        this.gameDataProxy = gameDataProxy;
    }

    public Future<?> process(Runnable runnable) {
        return eventLoopThread.execute(runnable);
    }

    public Future<?> process(Runnable runnable, SyncCache syncCache) {
        return eventLoopThread.execute(() -> {
            runnable.run();

            syncCache.process(generalExecutor, gameDataProxy);
        });
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void stop() {
        eventLoopThread.stop();
    }
}
