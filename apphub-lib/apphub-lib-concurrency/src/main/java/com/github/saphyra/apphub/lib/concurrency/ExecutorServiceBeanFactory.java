package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class ExecutorServiceBeanFactory {
    private volatile static ExecutorService DEFAULT_EXECUTOR;

    private final ErrorReporterService errorReporterService;

    public ExecutorServiceBean create() {
        return create(defaultExecutor());
    }

    public ExecutorServiceBean create(ExecutorService executorService) {
        return ExecutorServiceBean.builder()
            .executor(executorService)
            .errorReporterService(errorReporterService)
            .build();
    }

    public ScheduledExecutorServiceBean createScheduled(int threadCount) {
        return ScheduledExecutorServiceBean.builder()
            .errorReporterService(errorReporterService)
            .executor(Executors.newScheduledThreadPool(threadCount))
            .build();
    }

    private static synchronized ExecutorService defaultExecutor() {
        if (isNull(DEFAULT_EXECUTOR)) {
            DEFAULT_EXECUTOR = Executors.newCachedThreadPool();
        }

        return DEFAULT_EXECUTOR;
    }
}
