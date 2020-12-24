package com.github.saphyra.apphub.lib.common_util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class ExecutorServiceBean implements Executor {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final SleepService sleepService;

    @Override
    public void execute(Runnable command) {
        executor.submit(command);
    }

    public <I, R> List<R> processCollectionWithWait(Collection<I> dataList, Function<I, R> mapper) {
        log.debug("Processing {} items...", dataList.size());

        List<Future<R>> futures = dataList.stream()
            .map(i -> executor.submit(() -> mapper.apply(i)))
            .collect(Collectors.toList());

        long inProgress;
        do {
            inProgress = futures.stream()
                .filter(rFuture -> !rFuture.isDone())
                .count();

            if (inProgress > 0) {
                log.debug("Incomplete tasks: {} out of {}", inProgress, dataList.size());
                sleepService.sleep(1);
            }
        } while (inProgress > 0);

        return futures.stream()
            .map(rFuture -> {
                try {
                    return rFuture.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());
    }
}
