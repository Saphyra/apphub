package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
public class ExecutorServiceBean {
    @NonNull
    private final ExecutorService executor;

    @NonNull
    private final ErrorReporterService errorReporterService;

    public Future<ExecutionResult<Void>> execute(Runnable command) {
        return asyncProcess(() -> {
            command.run();
            return null;
        });
    }

    public <T> Future<ExecutionResult<T>> asyncProcess(Callable<T> command) {
        return executor.submit(wrap(command));
    }

    private <T> Callable<ExecutionResult<T>> wrap(Callable<T> command) {
        return () -> {
            try {
                return new ExecutionResult<>(command.call(), null, true);
            } catch (Exception e) {
                log.error("Unexpected error during processing:", e);
                errorReporterService.report("Unexpected error during processing: " + e.getMessage(), e);
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
            throw ExceptionFactory.reportedException("Parallelism must not be lower than 1. It was " + parallelism);
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
                .collect(Collectors.toList());

        List<R> results = new ArrayList<>();
        for (Future<ExecutionResult<R>> future : futures) {
            try {
                results.add(future.get().getOrThrow());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return results;
    }

    public void stop() {
        executor.shutdownNow();
    }
}
