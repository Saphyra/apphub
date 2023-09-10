package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.service.skyxplore.game.ws.handler.SkyXploreGameWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class MessageCacheFactory {
    private final SkyXploreGameWebSocketHandler webSocketHandler;

    MessageCache create() {
        return new MessageCache(webSocketHandler);
    }
}
