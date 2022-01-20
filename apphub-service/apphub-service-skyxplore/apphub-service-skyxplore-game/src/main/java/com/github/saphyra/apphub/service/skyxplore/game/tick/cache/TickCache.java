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
//TODO unit test
public class TickCache extends ConcurrentHashMap<UUID, TickCacheItem> {
    public void process(UUID gameId, GameDataProxy gameDataProxy, ExecutorServiceBean executorServiceBean) {
        get(gameId)
            .process(gameDataProxy, executorServiceBean);
    }
}
