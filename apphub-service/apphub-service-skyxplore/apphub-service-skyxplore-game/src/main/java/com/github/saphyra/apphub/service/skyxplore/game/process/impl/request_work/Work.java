package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.GameSleepService;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.Callable;

@Data
@Builder
public class Work implements Callable<Work> {
    private final int workPoints;
    private final Game game;
    private final Planet planet;
    private final UUID citizenId;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public Work call() {
        int processTime = calculateSleepTime();

        applicationContextProxy.getBean(GameSleepService.class)
            .sleep(game, processTime);

        game.getEventLoop()
            .process(() -> updateCitizen());
        return this;
    }

    private void updateCitizen() {
        //TODO decrease morale, increase skill
    }

    private int calculateSleepTime() {
        return 0; //TODO
    }
}
