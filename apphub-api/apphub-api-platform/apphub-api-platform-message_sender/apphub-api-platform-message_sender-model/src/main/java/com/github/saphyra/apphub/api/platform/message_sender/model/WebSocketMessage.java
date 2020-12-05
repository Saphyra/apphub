package com.github.saphyra.apphub.api.platform.message_sender.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WebSocketMessage {
    private List<UUID> recipients;
    private WebSocketEvent event;
}
