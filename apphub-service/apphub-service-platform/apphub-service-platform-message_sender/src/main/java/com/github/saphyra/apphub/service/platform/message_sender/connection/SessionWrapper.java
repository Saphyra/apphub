package com.github.saphyra.apphub.service.platform.message_sender.connection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Data
class SessionWrapper {
    private final WebSocketSession session;
    private LocalDateTime lastUpdate;
}
