package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncCacheFactory {
    private final GameItemCacheFactory gameItemCacheFactory;
    private final MessageCacheFactory messageCacheFactory;

    public SyncCache create() {
        return SyncCache.builder()
            .gameItemCache(gameItemCacheFactory.create())
            .messageCache(messageCacheFactory.create())
            .build();
    }
}
