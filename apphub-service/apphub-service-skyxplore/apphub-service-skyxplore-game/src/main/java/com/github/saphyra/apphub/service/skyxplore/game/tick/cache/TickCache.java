package com.github.saphyra.apphub.service.skyxplore.game.tick.cache;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class TickCache extends ConcurrentHashMap<UUID, TickCacheItem> {
    private final GameDataProxy gameDataProxy;
    private final ExecutorServiceBean executorServiceBean;

    public void process(UUID gameId) {
        get(gameId)
            .process(gameDataProxy, executorServiceBean);
    }
}
