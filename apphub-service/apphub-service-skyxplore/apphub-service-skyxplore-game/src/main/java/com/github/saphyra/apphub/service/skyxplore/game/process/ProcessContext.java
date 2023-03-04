package com.github.saphyra.apphub.service.skyxplore.game.process;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.process.background.morale.ActiveMoraleRechargeService;
import com.github.saphyra.apphub.service.skyxplore.game.process.background.morale.PassiveMoraleRechargeService;
import com.github.saphyra.apphub.service.skyxplore.game.process.background.satiety.SatietyDecreaseService;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
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
    private final SatietyDecreaseService satietyDecreaseService;
    private final PassiveMoraleRechargeService passiveMoraleRechargeService;
    private final ActiveMoraleRechargeService activeMoraleRechargeService;
}
