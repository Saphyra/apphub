package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class MessageCacheFactory {
    private final MessageSenderProxy messageSenderProxy;

    MessageCache create() {
        return new MessageCache(messageSenderProxy);
    }
}
