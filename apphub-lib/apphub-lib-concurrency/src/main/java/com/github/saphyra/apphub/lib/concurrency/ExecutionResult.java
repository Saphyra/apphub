package com.github.saphyra.apphub.lib.concurrency;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class ExecutionResult<T> {
    private final T value;
    private final Exception exception;
    private final boolean success;

    public static <T> ExecutionResult<T> success(T value) {
        return new ExecutionResult<>(value, null, true);
    }

    public static <T> ExecutionResult<T> failure(Exception e) {
        return new ExecutionResult<>(null, e, false);
    }

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
