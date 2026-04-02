package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.client.MonitoringClient;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DefaultCollectedMetricClientTest {
    private static final String SERVICE = "service";

    @Mock
    private MonitoringClient monitoringClient;

    @InjectMocks
    private DefaultCollectedMetricClient underTest;

    @Mock
    private PutMetricsRequest request;

    @BeforeEach
    void setUp() {
        underTest = new DefaultCollectedMetricClient(monitoringClient, SERVICE);
    }

    @Test
    void send() {
        underTest.send(List.of(request));

        then(monitoringClient).should().reportMetrics(SERVICE, List.of(request));
    }
}