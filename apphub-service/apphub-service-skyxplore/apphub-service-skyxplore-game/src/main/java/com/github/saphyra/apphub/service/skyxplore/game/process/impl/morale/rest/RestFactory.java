package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest;

import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestFactory {
    private final ApplicationContextProxy applicationContextProxy;

    public Rest create(int restoredMorale, int sleepTime, Game game) {
        return Rest.builder()
            .restoredMorale(restoredMorale)
            .sleepTime(sleepTime)
            .game(game)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
