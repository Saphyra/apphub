package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
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
                .eventName(EmptyEvent.ADMIN_PANEL_TRIGGER_MEMORY_STATUS_UPDATE)
                .url(Endpoints.EVENT_MEMORY_MONITORING)
                .build()
        );
    }
}
