package com.github.saphyra.apphub.service.custom.elite_base.common;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractBuffer;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class BufferSynchronizationService {
    private final ScheduledExecutorServiceBean scheduledExecutorServiceBean;
    private final ExecutorServiceBean executorServiceBean;
    private final EliteBaseProperties properties;
    private final List<AbstractBuffer<?>> buffers;
    private final DateTimeUtil dateTimeUtil;
    private final PerformanceReporter performanceReporter;

    public void synchronize() {
        log.debug("Checking if buffers need synchronization");

        performanceReporter.wrap(
            () -> doSynchronize(),
            PerformanceReportingTopic.ELITE_BASE_BUFFER_SYNCHRONIZATION,
            PerformanceReportingKey.BUFFER_SYNCHRONIZATION_BATCH.name()
        );
    }

    @PreDestroy
    @SneakyThrows
    public void synchronizeAll() {
        log.info("Force-synchronizing all buffers");
        List<FutureWrapper<Void>> futures =  buffers.stream()
            .map(abstractBuffer -> executorServiceBean.execute(() -> doSynchronize(abstractBuffer)))
            .toList();

        for (FutureWrapper<Void> future : futures) {
            future.get()
                .getOrThrow();
        }
        log.info("Force-synchronization of all buffers completed");
    }

    @SneakyThrows
    private void doSynchronize() {
        List<FutureWrapper<Void>> futures = buffers.stream()
            .map(buffer -> executorServiceBean.execute(() -> synchronize(buffer)))
            .toList();

        for (FutureWrapper<Void> future : futures) {
            future.get()
                .getOrThrow();
        }
    }

    private void synchronize(AbstractBuffer<?> buffer) {
        CacheProperties cacheProperties = properties.getCache();

        int bufferSize = buffer.getSize();
        if (bufferSize > cacheProperties.getMaxBufferSize() || buffer.getLastSynchronized().plus(cacheProperties.getBufferSynchronizationInterval()).isBefore(dateTimeUtil.getCurrentDateTime())) {
            log.info("Synchronizing buffer: {}. BufferSize: {}, LastSynchronized: {}", buffer.getClass().getSimpleName(), bufferSize, buffer.getLastSynchronized());

            doSynchronize(buffer);
        }
    }

    private void doSynchronize(AbstractBuffer<?> buffer) {
        performanceReporter.wrap(
            buffer::synchronize,
            PerformanceReportingTopic.ELITE_BASE_BUFFER_SYNCHRONIZATION,
            PerformanceReportingKey.BUFFER_SYNCHRONIZATION.formatted(buffer.getClass().getSimpleName())
        );
    }

    @PostConstruct
    void schedule() {
        Duration interval = properties.getCache()
            .getBufferSynchronizationCheckInterval();

        scheduledExecutorServiceBean.scheduleWithFixedDelay(this::synchronize, interval);
    }
}
