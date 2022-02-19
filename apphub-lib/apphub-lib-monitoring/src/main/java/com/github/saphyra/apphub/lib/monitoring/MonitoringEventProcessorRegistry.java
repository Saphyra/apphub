package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MonitoringEventProcessorRegistry implements EventProcessorRegistry {
    private final String serviceName;

    public MonitoringEventProcessorRegistry(
        @Value("${spring.application.name}") String serviceName
    ) {
        this.serviceName = serviceName;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(EmptyEvent.ADMIN_PANEL_TRIGGER_MEMORY_STATUS_UPDATE)
                .url(Endpoints.EVENT_MEMORY_MONITORING)
                .build()
        );
    }
}
