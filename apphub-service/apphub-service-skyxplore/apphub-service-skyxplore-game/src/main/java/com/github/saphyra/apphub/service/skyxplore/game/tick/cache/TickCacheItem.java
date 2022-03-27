package com.github.saphyra.apphub.service.skyxplore.game.tick.cache;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.Data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class TickCacheItem {
    private final GameItemCache gameItemCache = new GameItemCache();
    private final MessageCache messageCache = new MessageCache();
    private final Map<UUID, Assignment> citizenAssignments = new ConcurrentHashMap<>();

    void process(GameDataProxy gameDataProxy, ExecutorServiceBean executorServiceBean) {
        gameItemCache.process(gameDataProxy);
        messageCache.process(executorServiceBean);
    }
}
