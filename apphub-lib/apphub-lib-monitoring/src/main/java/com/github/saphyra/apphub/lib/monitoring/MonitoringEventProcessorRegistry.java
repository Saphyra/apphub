package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.MonitoringEndpoints;
import com.github.saphyra.apphub.lib.event.MonitoringEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MonitoringEventProcessorRegistry implements EventProcessorRegistry {
    private final String host;

    public MonitoringEventProcessorRegistry(
        @Value("${event.serviceHost}") String host
    ) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(MonitoringEvent.REPORT_MEMORY_STATUS)
                .url(MonitoringEndpoints.EVENT_REPORT_MEMORY_STATUS)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(MonitoringEvent.SEND_COLLECTED_METRICS)
                .url(MonitoringEndpoints.EVENT_SEND_COLLECTED_METRICS)
                .build()
        );
    }
}
