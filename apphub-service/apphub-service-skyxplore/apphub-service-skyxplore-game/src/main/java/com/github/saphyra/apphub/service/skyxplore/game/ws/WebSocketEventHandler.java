package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;

import java.util.UUID;

public interface WebSocketEventHandler {
    boolean canHandle(WebSocketEventName eventName);

    void handle(UUID from, WebSocketEvent event);
}
