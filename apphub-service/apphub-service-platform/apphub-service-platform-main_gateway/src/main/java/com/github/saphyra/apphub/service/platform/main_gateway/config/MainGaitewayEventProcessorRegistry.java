package com.github.saphyra.apphub.service.platform.main_gateway.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.UserEndpoints;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainGaitewayEventProcessorRegistry implements EventProcessorRegistry {
    private final String host;

    public MainGaitewayEventProcessorRegistry(
        @Value("${event.serviceHost}") String host
    ) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .host(host)
                .url(UserEndpoints.EVENT_ACCESS_TOKEN_INVALIDATED)
                .eventName(EmptyEvent.ACCESS_TOKENS_INVALIDATED)
                .build()
        );
    }
}
