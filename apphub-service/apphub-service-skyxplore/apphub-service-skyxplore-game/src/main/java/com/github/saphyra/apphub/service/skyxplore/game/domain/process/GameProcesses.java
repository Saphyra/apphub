package com.github.saphyra.apphub.service.skyxplore.game.domain.process;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameProcesses {
    private final SatietyDecreaseProcess satietyDecreaseProcess;

    public GameProcesses(Game game, ProcessContext processContext) {
        log.info("Initializing GameProcess for game {}", game.getGameId());
        satietyDecreaseProcess = new SatietyDecreaseProcess(game, processContext);
    }
}
