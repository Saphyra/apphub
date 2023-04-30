package com.github.saphyra.apphub.api.platform.message_sender.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WebSocketMessage {
    private Collection<UUID> recipients;
    private WebSocketEvent event;

    public static WebSocketMessage forEventAndRecipients(WebSocketEventName eventName, UUID recipient, Object payload) {
        return forEventAndRecipients(eventName, List.of(recipient), payload);
    }

    public static WebSocketMessage forEventAndRecipients(WebSocketEventName eventName, Collection<UUID> recipients) {
        return forEventAndRecipients(eventName, recipients, null);
    }

    public static WebSocketMessage forEventAndRecipients(WebSocketEventName eventName, Collection<UUID> recipients, Object payload) {
        return builder()
            .recipients(recipients)
            .event(WebSocketEvent.builder().eventName(eventName).payload(payload).build())
            .build();
    }
}
