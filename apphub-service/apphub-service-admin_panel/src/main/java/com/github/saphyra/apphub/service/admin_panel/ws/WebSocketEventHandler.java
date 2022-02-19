package com.github.saphyra.apphub.service.admin_panel.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import org.springframework.stereotype.Component;

import java.util.UUID;

public interface WebSocketEventHandler {
    boolean canHandle(WebSocketEventName eventName);

    void handle(UUID from, WebSocketEvent event);

    @Component
    class PingWebSocketEventHandler implements WebSocketEventHandler {

        @Override
        public boolean canHandle(WebSocketEventName eventName) {
            return eventName == WebSocketEventName.PING;
        }

        @Override
        public void handle(UUID from, WebSocketEvent event) {

        }
    }
}
