package com.github.saphyra.apphub.service.skyxplore.data.config;

import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.service.skyxplore.data.ws.load_game.LoadGameWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final LoadGameWebSocketHandler loadGameWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("Registering WebSocket handlers...");
        registry.addHandler(loadGameWebSocketHandler, GenericSkyXploreEndpoints.WS_CONNECTION_SKYXPLORE_INTERNAL);
    }
}
