package com.github.saphyra.apphub.integration.framework.concurrent;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class FutureWrapper<T> {
    private final Future<ExecutionResult<T>> future;

    @SneakyThrows
    public ExecutionResult<T> get() {
        return future.get();
    }

    @SneakyThrows
    public ExecutionResult<T> get(long timeout, TimeUnit timeUnit) {
        return future.get(timeout, timeUnit);
    }
}
