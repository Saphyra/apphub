package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest;

import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.service.GameSleepService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Builder
@Data
public class Rest implements Callable<Rest> {
    private final int restoredMorale;
    private final int sleepTime;
    private final Game game;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public Rest call() throws Exception {
        applicationContextProxy.getBean(GameSleepService.class)
            .sleep(game, sleepTime);

        return this;
    }
}
