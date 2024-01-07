package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncCacheFactory {
    private final GameItemCacheFactory gameItemCacheFactory;

    public SyncCache create() {
        return SyncCache.builder()
            .gameItemCache(gameItemCacheFactory.create())
            .build();
    }
}
