package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.exception.LoggedException;
import com.github.saphyra.apphub.lib.exception.NotLoggedException;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
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
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Builder(access = AccessLevel.PACKAGE)
public class ExecutorServiceBean {
    @NonNull
    @Getter
    private final ExecutorService executor;

    @NonNull
    private final ErrorReporterService errorReporterService;

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
                return ExecutionResult.success(command.call());
            } catch (NotLoggedException e) {
                log.warn("Exception occurred during async processing: {}", e.getMessage());
                return ExecutionResult.failure(e);
            } catch (LoggedException e) {
                log.error("Exception occurred during async processing", e);
                return ExecutionResult.failure(e);
            } catch (Exception e) {
                errorReporterService.report("Unexpected error during processing: " + e.getMessage(), e);
                return ExecutionResult.failure(e);
            }
        };
    }

    public <I> void forEach(List<I> dataList, Consumer<I> mapper) {
        processCollectionWithWait(dataList, i -> {
            mapper.accept(i);
            return null;
        });
    }

    /**
     * Applies the mapper on each item of dataList
     *
     * @param dataList    the data to process
     * @param mapper      mapping the data
     * @param parallelism max batch size
     * @param <I>         type of the input
     * @param <R>         type of the output
     * @return the processed list
     */
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

    /**
     * Splits the input to smaller batches, and processes each batch parallel
     *
     * @param input     the data to process
     * @param mapper    processing the data
     * @param batchSize size of each batch
     * @param <I>       type of the input
     * @param <R>       type of the output
     * @return the processed list
     */
    public <I, R> List<R> processBatch(List<I> input, Function<List<I>, List<R>> mapper, int batchSize, int threadCount) {
        List<List<I>> batches = Lists.partition(input, batchSize);

        Semaphore semaphore = new Semaphore(threadCount);

        return processCollectionWithWait(
            batches,
            batch -> {
                try {
                    semaphore.acquire();
                    return mapper.apply(batch);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            }
        )
            .stream()
            .flatMap(List::stream)
            .toList();
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
