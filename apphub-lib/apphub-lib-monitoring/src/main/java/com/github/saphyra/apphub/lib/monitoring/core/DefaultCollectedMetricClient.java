package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.client.MonitoringClient;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DefaultCollectedMetricClient implements CollectedMetricClient {
    private final MonitoringClient monitoringClient;
    private final String service;

    public DefaultCollectedMetricClient(MonitoringClient monitoringClient, String service) {
        this.monitoringClient = monitoringClient;
        this.service = service;
    }

    @Override
    public void send(List<PutMetricsRequest> requests) {
        monitoringClient.reportMetrics(service, requests);
    }
}
