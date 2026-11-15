package com.github.saphyra.apphub.service.platform.monitoring.controller;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequestWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeeaTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics.PutMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MonitoringMetricMessageListenerTest {
    private static final byte[] BODY = new byte[0];
    private static final String SERVICE = "service";

    @Mock
    private ObjectMapper objectMapper;

    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeeaTestUtils.create(mock(ErrorReporterService.class));

    @Mock
    private PutMetricsService putMetricsService;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private MonitoringMetricMessageListener underTest;

    @Mock
    private Message message;

    @Mock
    private PutMetricsRequestWrapper wrapper;

    @Mock
    private PutMetricsRequest request1;

    @Mock
    private PutMetricsRequest request2;

    @Test
    void onMessage() {
        given(message.getBody()).willReturn(BODY);
        given(objectMapper.readValue(BODY, PutMetricsRequestWrapper.class)).willReturn(wrapper);
        given(wrapper.getService()).willReturn(SERVICE);
        given(wrapper.getRequests()).willReturn(List.of(request1, request2));
        doAnswer(_ -> null).when(putMetricsService).putMetrics(SERVICE, request1);
        doThrow(new RuntimeException()).when(putMetricsService).putMetrics(SERVICE, request2);

        underTest.onMessage(message);

        then(putMetricsService).should().putMetrics(SERVICE, request1);
        then(putMetricsService).should().putMetrics(SERVICE, request2);
        then(errorReporterService).should().report(anyString(), any(RuntimeException.class));
    }
}