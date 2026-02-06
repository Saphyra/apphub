package com.github.saphyra.apphub.lib.concurrency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class FutureWrapper<T> {
    @Getter
    private final Future<ExecutionResult<T>> future;

    @SneakyThrows
    public ExecutionResult<T> get() {
        return future.get();
    }

    @SneakyThrows
    public ExecutionResult<T> get(long timeout, TimeUnit timeUnit) {
        return future.get(timeout, timeUnit);
    }

    public boolean isDone() {
        return future.isDone();
    }
}
