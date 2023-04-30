package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
public class ScheduledExecutorServiceBean {
    @NonNull
    private final ScheduledExecutorService executor;

    @NonNull
    private final SleepService sleepService;

    @NonNull
    private final ErrorReporterService errorReporterService;

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, int initialDelay, int delay, TimeUnit timeUnit) {
        return executor.scheduleWithFixedDelay(wrap(task), initialDelay, delay, timeUnit);
    }

    private Runnable wrap(Runnable command) {
        return () -> {
            try {
                command.run();
            } catch (Exception e) {
                log.error("Unexpected error during processing:", e);
                errorReporterService.report("Unexpected error during processing: " + e.getMessage(), e);
            }
        };
    }
}
