package com.github.saphyra.apphub.service.skyxplore.game.process.background;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import com.github.saphyra.apphub.service.skyxplore.game.process.background.morale.MoraleRechargeBackgroundProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.background.satiety.SatietyDecreaseProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BackgroundProcessStarterService {
    private final ProcessContext processContext;

    public void startBackgroundProcesses(Game game) {
        new SatietyDecreaseProcess(game, processContext)
            .startProcess();
        new ProcessSchedulerProcess(game, processContext)
            .startProcess();
        new FinishedProcessCleanupProcess(game, processContext)
            .startProcess();
        new MoraleRechargeBackgroundProcess(game, processContext)
            .startProcess();
    }
}
