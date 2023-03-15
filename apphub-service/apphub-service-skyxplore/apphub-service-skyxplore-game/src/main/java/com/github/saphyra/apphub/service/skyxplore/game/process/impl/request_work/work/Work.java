package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.GameSleepService;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Data
@Builder
@Slf4j
@ToString(exclude = {"game", "planet", "applicationContextProxy"})
public class Work implements Callable<Work> {
    private final int workPoints;
    private final Game game;
    private final Planet planet;
    private final UUID citizenId;
    private final SkillType skillType;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public Work call() {
        long processTime = applicationContextProxy.getBean(SleepTimeCalculator.class)
            .calculateSleepTime(planet, citizenId, skillType, workPoints);
        log.info("SleepTime for workPoints {}: {}", workPoints, processTime);

        applicationContextProxy.getBean(GameSleepService.class)
            .sleep(game, processTime);

        SyncCache syncCache = applicationContextProxy.getBean(SyncCacheFactory.class)
            .create();

        Future<?> citizenUpdateProcess = game.getEventLoop()
            .process(() -> applicationContextProxy.getBean(CitizenUpdateService.class).updateCitizen(syncCache, game.getGameId(), planet, citizenId, workPoints, skillType), syncCache);

        while (!citizenUpdateProcess.isDone()) {
            applicationContextProxy.getBean(SleepService.class)
                .sleep(100);
        }
        return this;
    }
}
