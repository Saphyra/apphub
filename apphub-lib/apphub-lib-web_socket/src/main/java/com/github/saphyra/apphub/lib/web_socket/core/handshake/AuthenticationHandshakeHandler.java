package com.github.saphyra.apphub.lib.web_socket.core.handshake;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationHandshakeHandler extends DefaultHandshakeHandler {
    private final WsSessionUserIdProvider userIdProvider;
    private final UuidConverter uuidConverter;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("Connecting to {} / {}", request.getRemoteAddress(), request.getLocalAddress());
        log.debug("Attributes: {}", attributes);
        Optional<UUID> userId = userIdProvider.findUserId(request);
        log.info("{} is connected.", userId);
        return () -> uuidConverter.convertDomain(userId).orElse(null);
    }
}
