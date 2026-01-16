package com.github.saphyra.apphub.service.custom.elite_base.util;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TimeoutUtil {
    private final ExecutorServiceBean executorServiceBean;

    @SneakyThrows
    public <T> T withTimeout(Supplier<T> supplier, Duration timeout, String errorMessage) {
        Future<ExecutionResult<T>> future = executorServiceBean.asyncProcess(supplier::get)
            .getFuture();

        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .getOrThrow();
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public void withTimeout(Runnable task, Duration timeout, String errorMessage) {
        withTimeout(
            () -> {
                task.run();
                return null;
            },
            timeout,
            errorMessage
        );
    }
}
