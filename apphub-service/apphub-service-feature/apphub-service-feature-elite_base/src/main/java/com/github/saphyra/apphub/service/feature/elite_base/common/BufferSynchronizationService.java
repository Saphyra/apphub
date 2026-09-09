package com.github.saphyra.apphub.service.feature.elite_base.common;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractBuffer;
import com.github.saphyra.apphub.lib.common_util.dao.Buffer;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.monitoring.instrument.MonitoringInstruments;
import jakarta.annotation.PreDestroy;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class BufferSynchronizationService {
    private final ScheduledExecutorServiceBean scheduledExecutorServiceBean;
    private final ExecutorServiceBean executorServiceBean;
    private final EliteBaseProperties properties;
    private final List<AbstractBuffer<?>> buffers;
    private final DateTimeUtil dateTimeUtil;
    private final MonitoringInstruments monitoringInstruments;

    @Builder
    public BufferSynchronizationService(
        ScheduledExecutorServiceBean scheduledExecutorServiceBean,
        ExecutorServiceBeanFactory executorServiceBeanFactory,
        EliteBaseProperties properties,
        List<AbstractBuffer<?>> buffers,
        DateTimeUtil dateTimeUtil,
        MonitoringInstruments monitoringInstruments
    ) {
        this.scheduledExecutorServiceBean = scheduledExecutorServiceBean;
        this.executorServiceBean = executorServiceBeanFactory.createFixed(1);
        this.properties = properties;
        this.buffers = buffers;
        this.dateTimeUtil = dateTimeUtil;
        this.monitoringInstruments = monitoringInstruments;
    }

    public void synchronize() {
        log.debug("Checking if buffers need synchronization");

        monitoringInstruments.wrap(
            () -> doSynchronize(),
            Feature.ELITE_BASE_BUFFER_SYNCHRONIZATION,
            PerformanceReportingKey.BUFFER_SYNCHRONIZATION_BATCH.name()
        );
    }

    @PreDestroy
    @SneakyThrows
    public void synchronizeAll() {
        log.info("Force-synchronizing all buffers");
        List<FutureWrapper<Void>> futures = buffers.stream()
            .sorted(Comparator.comparingInt(Buffer::getOrder))
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
            .sorted(Comparator.comparingInt(Buffer::getOrder))
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
        monitoringInstruments.wrap(
            buffer::synchronize,
            Feature.ELITE_BASE_BUFFER_SYNCHRONIZATION,
            PerformanceReportingKey.BUFFER_SYNCHRONIZATION.formatted(buffer.getClass().getSimpleName())
        );
    }

    @EventListener(ApplicationReadyEvent.class)
    void schedule() {
        Duration interval = properties.getCache()
            .getBufferSynchronizationCheckInterval();

        scheduledExecutorServiceBean.scheduleWithFixedDelay(this::synchronize, interval);
    }
}
