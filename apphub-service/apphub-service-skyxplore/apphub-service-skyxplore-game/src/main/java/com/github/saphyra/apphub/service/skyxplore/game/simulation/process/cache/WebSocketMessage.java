package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class WebSocketMessage {
    private final List<UUID> recipients;
    private final WebSocketEvent event;

    public static WebSocketMessage forEventAndRecipients(WebSocketEventName eventName, UUID recipient, Object payload) {
        return builder()
            .recipients(List.of(recipient))
            .event(WebSocketEvent.builder()
                .eventName(eventName)
                .payload(payload)
                .build()
            )
            .build();
    }
}
