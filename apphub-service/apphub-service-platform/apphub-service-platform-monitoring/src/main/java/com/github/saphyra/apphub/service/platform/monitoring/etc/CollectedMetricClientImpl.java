package com.github.saphyra.apphub.service.platform.monitoring.etc;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.monitoring.CollectedMetricClient;
import com.github.saphyra.apphub.service.platform.monitoring.controller.MonitoringControllerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
//TODO unit test
public class CollectedMetricClientImpl implements CollectedMetricClient {
    private final MonitoringControllerImpl monitoringController;
    private final String service;

    public CollectedMetricClientImpl(MonitoringControllerImpl monitoringController, @Value("${spring.application.name}") String service) {
        this.monitoringController = monitoringController;
        this.service = service;
    }

    @Override
    public void send(List<PutMetricsRequest> requests) {
        monitoringController.internalReportMetrics(service, requests);
    }
}
