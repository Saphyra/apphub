package com.github.saphyra.apphub.service.skyxplore.data.ws.load_game;

import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEventName;
import org.springframework.web.socket.WebSocketSession;

public interface SkyXploreLoadGameWsEventHandler {
    boolean canHandle(SkyXploreWsEventName eventName);

    void handle(LoadGameWebSocketHandler loadGameWebSocketHandler, WebSocketSession session, SkyXploreWsEvent event);
}
