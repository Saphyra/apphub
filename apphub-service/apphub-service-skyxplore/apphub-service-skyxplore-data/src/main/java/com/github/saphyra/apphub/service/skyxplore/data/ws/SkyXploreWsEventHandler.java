package com.github.saphyra.apphub.service.skyxplore.data.ws;

import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEventName;
import org.springframework.web.socket.WebSocketSession;

public interface SkyXploreWsEventHandler {
    boolean canHandle(SkyXploreWsEventName eventName);

    void handle(LoadGameWebSocketHandler loadGameWebSocketHandler, WebSocketSession session, SkyXploreWsEvent event);
}
