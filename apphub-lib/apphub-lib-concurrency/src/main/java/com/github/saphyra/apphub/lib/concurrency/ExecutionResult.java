package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;
import java.util.function.Consumer;

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

    //TODO unit test
    public BiWrapper<Boolean, T> getOrHandle(Consumer<Exception> errorHandler) {
        if (success) {
            return new BiWrapper<>(true, value);
        }

        Exception e = Optional.ofNullable(exception)
            .orElseGet(() -> new IllegalStateException("Both value and exception was null of this result."));

        errorHandler.accept(e);

        return new BiWrapper<>(false, null);
    }
}
