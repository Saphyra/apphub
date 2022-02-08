package com.github.saphyra.apphub.service.admin_panel.ws;

import com.github.saphyra.apphub.api.admin_panel.server.AdminPanelWsController;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AdminPanelWsControllerImpl implements AdminPanelWsController {
    private final ConnectedWsClients connectedWsClients;
    private final List<WebSocketEventHandler> handlers;

    @Override
    public void userConnected(UUID userId) {
        log.info("{} connected to WebSocket", userId);
        connectedWsClients.addIfAbsent(userId);
    }

    @Override
    public void userDisconnected(UUID userId) {
        log.info("{} disconnected from WebSocket", userId);
        connectedWsClients.remove(userId);
    }

    @Override
    public void processWebSocketEvent(UUID userId, WebSocketEvent event) {
        handlers.stream()
            .filter(webSocketEventHandler -> webSocketEventHandler.canHandle(event.getEventName()))
            .forEach(webSocketEventHandler -> webSocketEventHandler.handle(userId, event));
    }
}
