package com.github.saphyra.apphub.service.skyxplore.game.process;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.GameSleepService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
public class ProcessContext {
    private final ExecutorServiceBean executorServiceBean;
    private final GameSleepService gameSleepService;
    private final SleepService sleepService;
    private final SyncCacheFactory syncCacheFactory;
}
