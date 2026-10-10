package com.github.saphyra.apphub.service.platform.main_gateway.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.api.platform.main_gateway.model.MainGatewayEndpoints;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class MainGatewayEventProcessorRegistry implements EventProcessorRegistry {
    private final String host;

    public MainGatewayEventProcessorRegistry(
        @Value("${event.serviceHost}") String host
    ) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.PLATFORM_DISABLED_ROLES_CHANGED)
                .url(MainGatewayEndpoints.EVENT_DISABLED_ROLES_CHANGED)
                .build()
        );
    }
}
