package com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EventLoop {
    private final ExecutorServiceBean eventLoopThread;
    private final Collection<Runnable> queue;

    @Builder
    public EventLoop(ExecutorServiceBeanFactory executorServiceBeanFactory) {
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
    }

    @SneakyThrows
    public ExecutionResult<Void> processWithWait(Runnable runnable) {
        return process(runnable)
            .get(60, TimeUnit.SECONDS);
    }

    public Future<ExecutionResult<Void>> process(Runnable runnable) {
        return eventLoopThread.execute(runnable);
    }

    @SneakyThrows
    public <T> ExecutionResult<T> processWithResponseAndWait(Callable<T> callable) {
        return eventLoopThread.asyncProcess(callable)
            .get(60, TimeUnit.SECONDS);
    }

    public <T> Future<ExecutionResult<T>> processWithResponse(Callable<T> callable) {
        return eventLoopThread.asyncProcess(callable);
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void stop() {
        eventLoopThread.stop();
    }
}
