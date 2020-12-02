package com.github.saphyra.apphub.service.platform.message_sender.config;

import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.service.platform.message_sender.connection.SkyXploreMainMenuWebSocketHandler;
import com.github.saphyra.apphub.service.platform.message_sender.session.AuthenticationHandshakeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final AuthenticationHandshakeHandler authenticationHandshakeHandler;

    private final SkyXploreMainMenuWebSocketHandler skyXploreMainMenuWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(skyXploreMainMenuWebSocketHandler, Endpoints.CONNECTION_SKYXPLORE_MAIN_MENU)
            .setHandshakeHandler(authenticationHandshakeHandler);
    }
}
