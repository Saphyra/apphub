package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;

import java.util.UUID;

public interface WebSocketEventHandler {
    boolean canHandle(String eventName);

    void handle(UUID from, WebSocketEvent event);
}
