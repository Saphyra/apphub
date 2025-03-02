package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
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
    private final ErrorReporterService errorReporterService;

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, int initialDelay, int delay, TimeUnit timeUnit) {
        return executor.scheduleWithFixedDelay(wrap(task), initialDelay, delay, timeUnit);
    }

    public ScheduledFuture<?> scheduleFixedRate(Runnable task, Duration duration) {
        return scheduleFixedRate(task, duration.getSeconds(), TimeUnit.SECONDS);
    }

    public ScheduledFuture<?> scheduleFixedRate(Runnable task, long rate, TimeUnit timeUnit) {
        return executor.scheduleAtFixedRate(wrap(task), 0, rate, timeUnit);
    }

    public ScheduledFuture<?> schedule(Runnable task, Duration delay) {
        return executor.schedule(wrap(task), delay.getSeconds(), TimeUnit.SECONDS);
    }

    public void shutdown(){
        executor.shutdown();
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
