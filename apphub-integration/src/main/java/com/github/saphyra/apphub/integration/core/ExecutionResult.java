package com.github.saphyra.apphub.integration.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Data
public class ExecutionResult<T> {
    private final T value;
    private final Exception exception;
    private final boolean success;

    public T getOrThrow() {
        if (success) {
            return value;
        }

        throw Optional.ofNullable(exception)
            .map(e -> {
                if (e instanceof RuntimeException) {
                    return (RuntimeException) e;
                }

                return new RuntimeException(e);
            })
            .orElseGet(() -> new IllegalStateException("Both value and exception was null of this result."));
    }
}
