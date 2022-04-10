package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class MessageCacheFactory {
    private final ExecutorServiceBean executorServiceBean;

    MessageCache create() {
        return new MessageCache(executorServiceBean);
    }
}
