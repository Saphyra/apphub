package com.github.saphyra.apphub.lib.web_socket.core;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketEventProcessorRegistry implements EventProcessorRegistry {
    private final String host;

    public WebSocketEventProcessorRegistry(
        @Value("${event.serviceHost}") String host
    ) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(RegisterProcessorRequest.builder()
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
