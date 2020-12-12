package com.github.saphyra.apphub.service.platform.message_sender.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EventProcessorRegistryImpl implements EventProcessorRegistry {
    private final String serviceName;

    public EventProcessorRegistryImpl(
        @Value("${spring.application.name}") String serviceName
    ) {
        this.serviceName = serviceName;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return Arrays.asList(
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(EmptyEvent.MESSAGE_SENDER_PING_REQUEST_EVENT_NAME)
                .url(Endpoints.MESSAGE_SENDER_PING_REQUEST_EVENT)
                .build(),
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(EmptyEvent.MESSAGE_SENDER_CONNECTION_CLEANUP_EVENT)
                .url(Endpoints.MESSAGE_SENDER_CONNECTION_CLEANUP_EVENT)
                .build()
        );
    }
}