package com.github.saphyra.apphub.service.custom.elite_base.common;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractBuffer;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BufferSynchronizationServiceTest {
    private static final Duration BUFFER_SYNCHRONIZATION_INTERVAL = Duration.ofSeconds(2);
    private static final Integer MAX_BUFFER_SIZE = 23;
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Duration BUFFER_SYNCHRONIZATION_CHECK_INTERVAL = Duration.ofSeconds(3);

    @Mock
    private ScheduledExecutorServiceBean scheduledExecutorServiceBean;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private EliteBaseProperties properties;

    @Mock
    private AbstractBuffer buffer;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private PerformanceReporter performanceReporter;

    private BufferSynchronizationService underTest;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private CacheProperties cacheProperties;

    @BeforeEach
    void setUp() {
        underTest = BufferSynchronizationService.builder()
            .scheduledExecutorServiceBean(scheduledExecutorServiceBean)
            .executorServiceBean(executorServiceBean)
            .properties(properties)
            .buffers(List.of(buffer))
            .dateTimeUtil(dateTimeUtil)
            .performanceReporter(performanceReporter)
            .build();
    }

    @Test
    void synchronize_notNeeded() {
        given(properties.getCache()).willReturn(cacheProperties);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());
        given(executorServiceBean.execute(any(Runnable.class))).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return CompletableFuture.completedFuture(executionResult);
        });
        given(cacheProperties.getBufferSynchronizationInterval()).willReturn(BUFFER_SYNCHRONIZATION_INTERVAL);
        given(cacheProperties.getMaxBufferSize()).willReturn(MAX_BUFFER_SIZE);
        given(buffer.getSize()).willReturn(MAX_BUFFER_SIZE - 1);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(buffer.getLastSynchronized()).willReturn(CURRENT_TIME.minus(BUFFER_SYNCHRONIZATION_INTERVAL).plusSeconds(1));

        underTest.synchronize();

        then(buffer).should(times(0)).synchronize();
    }

    @Test
    void synchronize_bufferSize() {
        given(properties.getCache()).willReturn(cacheProperties);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());
        given(executorServiceBean.execute(any(Runnable.class))).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return CompletableFuture.completedFuture(executionResult);
        });
        given(cacheProperties.getMaxBufferSize()).willReturn(MAX_BUFFER_SIZE);
        given(buffer.getSize()).willReturn(MAX_BUFFER_SIZE + 1);
        given(buffer.getLastSynchronized()).willReturn(CURRENT_TIME.minus(BUFFER_SYNCHRONIZATION_INTERVAL).plusSeconds(1));

        underTest.synchronize();

        then(buffer).should().synchronize();
    }

    @Test
    void synchronize_intervalReached() {
        given(properties.getCache()).willReturn(cacheProperties);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());
        given(executorServiceBean.execute(any(Runnable.class))).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return CompletableFuture.completedFuture(executionResult);
        });
        given(cacheProperties.getBufferSynchronizationInterval()).willReturn(BUFFER_SYNCHRONIZATION_INTERVAL);
        given(cacheProperties.getMaxBufferSize()).willReturn(MAX_BUFFER_SIZE);
        given(buffer.getSize()).willReturn(MAX_BUFFER_SIZE - 1);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(buffer.getLastSynchronized()).willReturn(CURRENT_TIME.minus(BUFFER_SYNCHRONIZATION_INTERVAL).minusSeconds(1));

        underTest.synchronize();

        then(buffer).should().synchronize();
    }

    @Test
    void synchronizeAll() {
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());
        given(executorServiceBean.execute(any(Runnable.class))).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return CompletableFuture.completedFuture(executionResult);
        });

        underTest.synchronizeAll();

        then(buffer).should().synchronize();
    }

    @Test
    void schedule() {
        given(properties.getCache()).willReturn(cacheProperties);
        given(cacheProperties.getBufferSynchronizationCheckInterval()).willReturn(BUFFER_SYNCHRONIZATION_CHECK_INTERVAL);

        underTest.schedule();

        then(scheduledExecutorServiceBean).should().scheduleWithFixedDelay(any(), eq(BUFFER_SYNCHRONIZATION_CHECK_INTERVAL));
    }
}