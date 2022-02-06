package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageFactory {
    public WebSocketMessage create(UUID recipient, WebSocketEventName eventName, Object payload) {
        return create(List.of(recipient), eventName, payload);
    }

    public WebSocketMessage create(List<UUID> recipients, WebSocketEventName eventName, Object payload) {
        WebSocketEvent webSocketEvent = WebSocketEvent.builder()
            .eventName(eventName)
            .payload(payload)
            .build();
        return WebSocketMessage.builder()
            .recipients(recipients)
            .event(webSocketEvent)
            .build();
    }
}
