package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceReporter {
    private final PerformanceReportSender performanceReportSender;

    public void wrap(Runnable runnable, PerformanceReportingTopic topic, String key) {
        wrap(
            () -> {
                runnable.run();
                return null;
            },
            topic,
            key
        );
    }

    @SneakyThrows
    public <T> T wrap(Callable<T> callable, PerformanceReportingTopic topic, String key) {
        StopWatch stopWatch = StopWatch.createStarted();
        T result = callable.call();
        stopWatch.stop();
        performanceReportSender.sendReport(topic, key, stopWatch.getTime(TimeUnit.NANOSECONDS));
        return result;
    }
}
