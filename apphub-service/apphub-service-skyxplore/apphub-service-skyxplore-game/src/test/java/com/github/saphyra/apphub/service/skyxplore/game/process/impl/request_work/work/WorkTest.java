package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.service.GameSleepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WorkTest {
    private static final int WORK_POINTS = 234;
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Long SLEEP_TIME = 264L;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private SleepTimeCalculator sleepTimeCalculator;

    @Mock
    private GameSleepService gameSleepService;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock
    private CitizenUpdateService citizenUpdateService;

    @Mock
    private SleepService sleepService;

    private Work underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private SyncCache syncCache;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Future<ExecutionResult<Void>> future;

    @BeforeEach
    public void setUp() {
        underTest = Work.builder()
            .workPoints(WORK_POINTS)
            .game(game)
            .planet(planet)
            .citizenId(CITIZEN_ID)
            .skillType(SkillType.AIMING)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    public void call() {
        given(applicationContextProxy.getBean(SleepTimeCalculator.class)).willReturn(sleepTimeCalculator);
        given(applicationContextProxy.getBean(GameSleepService.class)).willReturn(gameSleepService);
        given(applicationContextProxy.getBean(SyncCacheFactory.class)).willReturn(syncCacheFactory);
        given(applicationContextProxy.getBean(CitizenUpdateService.class)).willReturn(citizenUpdateService);
        given(applicationContextProxy.getBean(SleepService.class)).willReturn(sleepService);

        given(sleepTimeCalculator.calculateSleepTime(planet, CITIZEN_ID, SkillType.AIMING, WORK_POINTS)).willReturn(SLEEP_TIME);
        given(syncCacheFactory.create()).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.process(any(Runnable.class), eq(syncCache))).willReturn(future);
        given(game.getGameId()).willReturn(GAME_ID);
        given(future.isDone())
            .willReturn(false)
            .willReturn(true);

        Work result = underTest.call();

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).process(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();
        verify(citizenUpdateService).updateCitizen(syncCache, GAME_ID, planet, CITIZEN_ID, WORK_POINTS, SkillType.AIMING);

        verify(gameSleepService).sleep(game, SLEEP_TIME);
        verify(sleepService, times(1)).sleep(100);

        assertThat(result).isEqualTo(underTest);
    }
}