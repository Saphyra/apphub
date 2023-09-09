package com.github.saphyra.apphub.lib.web_socket.core;

import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handshake.AuthenticationHandshakeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketRegistry implements WebSocketConfigurer {
    private final AuthenticationHandshakeHandler authenticationHandshakeHandler;
    private final List<AbstractWebSocketHandler> webSocketHandlers;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        webSocketHandlers.forEach(abstractWebSocketHandler ->
            registry.addHandler(abstractWebSocketHandler, abstractWebSocketHandler.getEndpoint())
                .setHandshakeHandler(authenticationHandshakeHandler)
                .setAllowedOrigins("*")
        );
    }
}
