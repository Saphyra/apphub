package com.github.saphyra.apphub.service.skyxplore.game.ws.main.handler;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;

import java.util.UUID;

public interface WebSocketEventHandler {
    boolean canHandle(WebSocketEventName eventName);

    void handle(UUID from, WebSocketEvent event, SkyXploreGameMainWebSocketHandler webSocketHandler);
}
