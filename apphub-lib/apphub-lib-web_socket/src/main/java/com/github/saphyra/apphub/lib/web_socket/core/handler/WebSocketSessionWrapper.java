package com.github.saphyra.apphub.lib.web_socket.core.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class WebSocketSessionWrapper {
    private final WebSocketSession session;
    private LocalDateTime lastUpdate;
}
