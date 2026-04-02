package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CollectedMetricSenderTest {
    @Mock
    private CollectedMetricClient collectedMetricClient;

    @Mock
    private MetricRegistry metricRegistry;

    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));

    @InjectMocks
    private CollectedMetricSender underTest;

    @Mock
    private PutMetricsRequest request;

    @Test
    void sendCollectedMetrics() {
        given(metricRegistry.getMetricsToSend()).willReturn(List.of(List.of(request)));

        underTest.sendCollectedMetrics();

        then(collectedMetricClient).should().send(List.of(request));
    }
}