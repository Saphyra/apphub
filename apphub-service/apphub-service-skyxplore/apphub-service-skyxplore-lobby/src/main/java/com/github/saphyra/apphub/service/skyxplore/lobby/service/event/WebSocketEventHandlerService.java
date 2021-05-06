package com.github.saphyra.apphub.service.skyxplore.lobby.service.event;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler.WebSocketEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventHandlerService {
    private final List<WebSocketEventHandler> handlers;

    public void handle(UUID from, WebSocketEvent event) {
        List<WebSocketEventHandler> eventHandlers = handlers.stream()
            .filter(webSocketEventHandler -> webSocketEventHandler.canHandle(event.getEventName()))
            .collect(Collectors.toList());

        eventHandlers.forEach(webSocketEventHandler -> webSocketEventHandler.handle(from, event));

        if (eventHandlers.isEmpty()) {
            log.warn("No {} found for event {}", WebSocketEventHandler.class.getName(), event.getEventName());
        }
    }
}
