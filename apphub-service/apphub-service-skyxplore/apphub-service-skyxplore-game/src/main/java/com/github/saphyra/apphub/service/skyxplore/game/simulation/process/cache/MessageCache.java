package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
//TODO unit test
class MessageCache extends ConcurrentHashMap<WsMessageKey, Supplier<WebSocketMessage>> {
    @NonNull
    private final MessageSenderProxy messageSenderProxy;

    void process(Game game) {
        List<WebSocketMessage> messages = entrySet()
            .stream()
            .filter(entry -> entry.getKey().shouldBeSent(game))
            .map(entry -> entry.getValue().get())
            .toList();

        messageSenderProxy.bulkSendToGame(messages);
    }
}
