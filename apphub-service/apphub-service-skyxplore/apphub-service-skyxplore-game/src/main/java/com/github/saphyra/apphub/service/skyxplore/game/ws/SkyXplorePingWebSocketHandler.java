package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
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
    public void handle(UUID from, WebSocketEvent event) {
        log.debug("Ping arrived from {}", from);
    }
}
