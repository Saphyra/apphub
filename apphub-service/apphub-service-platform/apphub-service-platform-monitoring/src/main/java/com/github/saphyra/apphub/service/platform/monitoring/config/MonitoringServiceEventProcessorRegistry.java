package com.github.saphyra.apphub.service.platform.monitoring.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.MonitoringEndpoints;
import com.github.saphyra.apphub.lib.event.MonitoringEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MonitoringServiceEventProcessorRegistry implements EventProcessorRegistry {
    private final String host;

    public MonitoringServiceEventProcessorRegistry(@Value("${event.serviceHost}") String host) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(MonitoringEvent.AGGREGATE_SECOND_METRICS)
                .url(MonitoringEndpoints.MONITORING_EVENT_AGGREGATE_SECOND_METRICS)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(MonitoringEvent.AGGREGATE_MINUTE_METRICS)
                .url(MonitoringEndpoints.MONITORING_EVENT_AGGREGATE_MINUTE_METRICS)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(MonitoringEvent.DELETE_EXPIRED_METRICS)
                .url(MonitoringEndpoints.MONITORING_EVENT_DELETE_EXPIRED_METRICS)
                .build()
        );
    }
}
