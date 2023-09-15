package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.game.ws.handler.WebSocketEventHandler;
import com.github.saphyra.apphub.service.skyxplore.game.ws.service.PlayerConnectedService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.service.PlayerDisconnectedService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class SkyXploreGameWebSocketHandler extends AbstractWebSocketHandler {
    private final List<WebSocketEventHandler> handlers;
    private final PlayerConnectedService playerConnectedService;
    private final PlayerDisconnectedService playerDisconnectedService;

    @Builder
    SkyXploreGameWebSocketHandler(
        WebSocketHandlerContext context,
        List<WebSocketEventHandler> handlers,
        PlayerConnectedService playerConnectedService,
        PlayerDisconnectedService playerDisconnectedService
    ) {
        super(context);
        this.handlers = handlers;
        this.playerConnectedService = playerConnectedService;
        this.playerDisconnectedService = playerDisconnectedService;
    }

    @Override
    public String getEndpoint() {
        return Endpoints.WS_CONNECTION_SKYXPLORE_GAME;
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event) {
        log.info("Processing WebSocketEvent {} from {}", event.getEventName(), userId);
        List<WebSocketEventHandler> eventHandlers = handlers.stream()
            .filter(webSocketEventHandler -> webSocketEventHandler.canHandle(event.getEventName()))
            .toList();

        eventHandlers.forEach(webSocketEventHandler -> webSocketEventHandler.handle(userId, event, this));

        if (eventHandlers.isEmpty()) {
            log.warn("No {} found for event {}", WebSocketEventHandler.class.getSimpleName(), event.getEventName());
        }
    }

    @Override
    protected void afterConnection(UUID userId) {
        log.info("{} joined to the game.", userId);
        playerConnectedService.playerConnected(userId, this);
    }

    @Override
    public void afterDisconnection(UUID userId) {
        log.info("{} left the game.", userId);
        playerDisconnectedService.playerDisconnected(userId, this);
    }
}
