package com.github.saphyra.apphub.service.platform.message_sender.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MessageSenderEventProcessorRegistry implements EventProcessorRegistry {
    private final String host;

    public MessageSenderEventProcessorRegistry(
        @Value("${event.serviceHost}") String host
    ) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return Arrays.asList(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.WEB_SOCKET_SEND_PING_EVENT)
                .url(Endpoints.EVENT_WEB_SOCKET_SEND_PING_EVENT)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.WEB_SOCKET_CONNECTION_CLEANUP_EVENT)
                .url(Endpoints.EVENT_WEB_SOCKET_CONNECTION_CLEANUP)
                .build()
        );
    }
}
