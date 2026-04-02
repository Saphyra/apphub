package com.github.saphyra.apphub.service.platform.monitoring.etc;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.service.platform.monitoring.controller.MonitoringControllerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CollectedMetricClientImplTest {
    private static final String SERVICE = "service";

    @Mock
    private MonitoringControllerImpl monitoringController;

    private CollectedMetricClientImpl underTest;

    @Mock
    private PutMetricsRequest request;

    @BeforeEach
    void setUp() {
        underTest = new CollectedMetricClientImpl(monitoringController, SERVICE);
    }

    @Test
    void send() {
        underTest.send(List.of(request));

        then(monitoringController).should().internalReportMetrics(SERVICE, List.of(request));
    }
}