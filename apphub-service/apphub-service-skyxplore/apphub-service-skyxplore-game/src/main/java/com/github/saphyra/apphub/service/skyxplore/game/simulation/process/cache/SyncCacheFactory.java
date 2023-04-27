package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncCacheFactory {
    private final GameItemCacheFactory gameItemCacheFactory;
    private final MessageCacheFactory messageCacheFactory;
    private final SyncCacheContext syncCacheContext;

    public SyncCache create(Game game) {
        return SyncCache.builder()
            .gameItemCache(gameItemCacheFactory.create())
            .messageCache(messageCacheFactory.create())
            .context(syncCacheContext)
            .game(game)
            .build();
    }
}
