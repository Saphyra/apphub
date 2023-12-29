package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
class MessageCache extends ConcurrentHashMap<WsMessageKey, Supplier<WebSocketMessage>> {
    @NonNull
    private final SkyXploreGameMainWebSocketHandler webSocketHandler;

    void process(Game game) {
        entrySet()
            .stream()
            .filter(entry -> entry.getKey().shouldBeSent(game))
            .map(entry -> entry.getValue().get())
            .forEach(webSocketMessage -> webSocketHandler.sendEvent(webSocketMessage.getRecipients(), webSocketMessage.getEvent()));
    }
}
