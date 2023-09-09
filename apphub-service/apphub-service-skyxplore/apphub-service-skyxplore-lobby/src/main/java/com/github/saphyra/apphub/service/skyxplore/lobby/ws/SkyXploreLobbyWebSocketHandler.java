package com.github.saphyra.apphub.service.skyxplore.lobby.ws;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.handler.WebSocketEventHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SkyXploreLobbyWebSocketHandler extends AbstractWebSocketHandler {
    private final List<WebSocketEventHandler> eventHandlers;

    public SkyXploreLobbyWebSocketHandler(WebSocketHandlerContext context, List<WebSocketEventHandler> eventHandlers) {
        super(context);
        this.eventHandlers = eventHandlers;
    }

    @Override
    public String getEndpoint() {
        return Endpoints.WS_CONNECTION_SKYXPLORE_LOBBY;
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event) {
        eventHandlers.stream()
            .filter(webSocketEventHandler -> webSocketEventHandler.canHandle(event.getEventName()))
            .forEach(webSocketEventHandler -> webSocketEventHandler.handle(userId, event, this));
    }
}
