package com.github.saphyra.apphub.service.skyxplore.game.ws.handler;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.ws.SkyXploreGameWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkyXplorePingWebSocketHandler implements WebSocketEventHandler {
    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return eventName == WebSocketEventName.PING;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event, SkyXploreGameWebSocketHandler webSocketHandler) {
        log.debug("Ping arrived from {}", from);
    }
}
