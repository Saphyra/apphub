package com.github.saphyra.apphub.integration.framework.concurrent;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Builder
public class ExecutorServiceBean {
    @NonNull
    private final ExecutorService executor;

    public FutureWrapper<Void> execute(Runnable command) {
        return asyncProcess(() -> {
            command.run();
            return null;
        });
    }

    public <T> FutureWrapper<T> asyncProcess(Callable<T> command) {
        return new FutureWrapper<>(executor.submit(wrap(command)));
    }

    private <T> Callable<ExecutionResult<T>> wrap(Callable<T> command) {
        return () -> {
            try {
                return new ExecutionResult<>(command.call(), null, true);
            } catch (Exception e) {
                log.error("Unexpected error during processing:", e);
                return new ExecutionResult<>(null, e, false);
            }
        };
    }

    public <I> void forEach(List<I> dataList, Consumer<I> mapper) {
        processCollectionWithWait(dataList, i -> {
            mapper.accept(i);
            return null;
        });
    }

    public <I, R> List<R> processCollectionWithWait(List<I> dataList, Function<I, R> mapper, int parallelism) {
        if (parallelism < 1) {
            throw new RuntimeException("Parallelism must not be lower than 1. It was " + parallelism);
        }

        return Lists.partition(dataList, parallelism)
            .stream()
            .map(part -> processCollectionWithWait(part, mapper))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public <I, R> List<R> processCollectionWithWait(Collection<I> dataList, Function<I, R> mapper) {
        log.debug("Processing {} items...", dataList.size());

        List<Future<ExecutionResult<R>>> futures = dataList.stream()
            .map(i -> executor.submit(wrap(() -> mapper.apply(i))))
            .toList();

        List<R> results = new ArrayList<>();
        for (Future<ExecutionResult<R>> future : futures) {
            results.add(getFutureResult(future));
        }

        return results;
    }

    @SneakyThrows
    private <R> R getFutureResult(Future<ExecutionResult<R>> future) {
        return future.get()
            .getOrThrow();
    }

    public void stop() {
        executor.shutdownNow();
    }
}
