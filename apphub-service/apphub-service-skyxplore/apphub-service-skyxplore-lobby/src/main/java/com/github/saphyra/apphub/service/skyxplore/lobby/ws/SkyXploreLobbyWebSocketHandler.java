package com.github.saphyra.apphub.service.skyxplore.lobby.ws;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.JoinToLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect.PlayerDisconnectedService;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.handler.WebSocketEventHandler;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class SkyXploreLobbyWebSocketHandler extends AbstractWebSocketHandler {
    private final List<WebSocketEventHandler> eventHandlers;
    private final ApplicationContextProxy applicationContextProxy;

    @Builder
    public SkyXploreLobbyWebSocketHandler(
        WebSocketHandlerContext context,
        List<WebSocketEventHandler> eventHandlers,
        ApplicationContextProxy applicationContextProxy
    ) {
        super(context);
        this.eventHandlers = eventHandlers;
        this.applicationContextProxy = applicationContextProxy;
    }

    @Override
    public String getEndpoint() {
        return Endpoints.WS_CONNECTION_SKYXPLORE_LOBBY;
    }

    @Override
    protected void afterConnection(UUID userId) {
        log.info("{} is joined to lobby", userId);
        applicationContextProxy.getBean(JoinToLobbyService.class)
            .userJoinedToLobby(userId);
    }

    @Override
    protected void afterDisconnection(UUID userId) {
        log.info("{} is disconnected from lobby", userId);
        applicationContextProxy.getBean(PlayerDisconnectedService.class)
            .playerDisconnected(userId);
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event) {
        eventHandlers.stream()
            .filter(webSocketEventHandler -> webSocketEventHandler.canHandle(event.getEventName()))
            .forEach(webSocketEventHandler -> webSocketEventHandler.handle(userId, event, this));
    }
}
