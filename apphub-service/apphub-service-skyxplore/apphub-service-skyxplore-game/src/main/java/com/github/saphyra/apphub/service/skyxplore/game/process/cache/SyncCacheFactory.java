package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SyncCacheFactory {
    private final ExecutorServiceBean executorServiceBean;
    private final GameDataProxy gameDataProxy;

    public SyncCache create() {
        return new SyncCache(executorServiceBean, gameDataProxy);
    }
}
