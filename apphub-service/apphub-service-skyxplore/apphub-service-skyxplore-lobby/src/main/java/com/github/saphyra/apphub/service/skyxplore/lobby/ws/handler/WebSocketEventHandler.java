package com.github.saphyra.apphub.service.skyxplore.lobby.ws.handler;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;

import java.util.UUID;

public interface WebSocketEventHandler {
    boolean canHandle(WebSocketEventName eventName);

    void handle(UUID from, WebSocketEvent event, SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler);
}
