package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameSleepService {
    private final SleepService sleepService;

    public void sleepASecond(Game game) {
        long timeout = 1000L;

        while (timeout > 0) {
            long sleep = Math.min(100, timeout);
            sleepService.sleep(sleep);
            if (game.shouldRun()) {
                timeout -= sleep;
            }
        }
    }
}
