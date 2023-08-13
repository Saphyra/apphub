package com.github.saphyra.apphub.integration.core;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.Future;

@AllArgsConstructor
public class FutureWrapper<T>{
    private final Future<ExecutionResult<T>> future;

    @SneakyThrows
    public ExecutionResult<T> get() {
        return future.get();
    }
}
