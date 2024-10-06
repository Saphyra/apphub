package com.github.saphyra.apphub.service.skyxplore.game.ws.main;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.handler.WebSocketEventHandler;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.service.PlayerConnectedService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.service.PlayerDisconnectedService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class SkyXploreGameMainWebSocketHandler extends AbstractWebSocketHandler {
    private final List<WebSocketEventHandler> handlers;
    private final PlayerConnectedService playerConnectedService;
    private final PlayerDisconnectedService playerDisconnectedService;

    @Builder
    SkyXploreGameMainWebSocketHandler(
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
        return GenericSkyXploreEndpoints.WS_CONNECTION_SKYXPLORE_GAME;
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event, String sessionId) {
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
    protected void afterConnection(UUID userId, String sessionId) {
        log.info("{} joined to the game.", userId);
        playerConnectedService.playerConnected(userId, this);
    }

    @Override
    public void afterDisconnection(UUID userId, String sessionId) {
        log.info("{} left the game.", userId);
        playerDisconnectedService.playerDisconnected(userId, this);
    }
}
