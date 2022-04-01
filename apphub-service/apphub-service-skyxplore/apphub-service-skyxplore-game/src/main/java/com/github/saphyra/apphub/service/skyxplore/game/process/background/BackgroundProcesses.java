package com.github.saphyra.apphub.service.skyxplore.game.process.background;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BackgroundProcesses {
    private final SatietyDecreaseProcess satietyDecreaseProcess;
    private final ProcessSchedulerProcess processSchedulerProcess;
    private final FinishedProcessCleanupProcess finishedProcessCleanupProcess;

    public BackgroundProcesses(Game game, ProcessContext processContext) {
        log.info("Initializing GameProcess for game {}", game.getGameId());
        satietyDecreaseProcess = new SatietyDecreaseProcess(game, processContext);
        processSchedulerProcess = new ProcessSchedulerProcess(game, processContext);
        finishedProcessCleanupProcess = new FinishedProcessCleanupProcess(game, processContext);
    }
}
