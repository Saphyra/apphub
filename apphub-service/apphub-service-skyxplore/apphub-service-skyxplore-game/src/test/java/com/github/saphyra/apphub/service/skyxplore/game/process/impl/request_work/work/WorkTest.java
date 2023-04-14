package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
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
    private static final UUID LOCATION = UUID.randomUUID();

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

    @Mock
    private GameData gameData;

    private Work underTest;

    @Mock
    private Game game;

    @Mock
    private SyncCache syncCache;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Future<ExecutionResult<Void>> future;

    @BeforeEach
    void setUp() {
        underTest = Work.builder()
            .workPoints(WORK_POINTS)
            .game(game)
            .location(LOCATION)
            .citizenId(CITIZEN_ID)
            .skillType(SkillType.AIMING)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void call() {
        given(game.getData()).willReturn(gameData);

        given(applicationContextProxy.getBean(SleepTimeCalculator.class)).willReturn(sleepTimeCalculator);
        given(applicationContextProxy.getBean(GameSleepService.class)).willReturn(gameSleepService);
        given(applicationContextProxy.getBean(SyncCacheFactory.class)).willReturn(syncCacheFactory);
        given(applicationContextProxy.getBean(CitizenUpdateService.class)).willReturn(citizenUpdateService);
        given(applicationContextProxy.getBean(SleepService.class)).willReturn(sleepService);

        given(sleepTimeCalculator.calculateSleepTime(gameData, CITIZEN_ID, SkillType.AIMING, WORK_POINTS)).willReturn(SLEEP_TIME);
        given(syncCacheFactory.create(game)).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.process(any(Runnable.class), eq(syncCache))).willReturn(future);
        given(future.isDone())
            .willReturn(false)
            .willReturn(true);

        Work result = underTest.call();

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).process(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();
        verify(citizenUpdateService).updateCitizen(syncCache, gameData, LOCATION, CITIZEN_ID, WORK_POINTS, SkillType.AIMING);

        verify(gameSleepService).sleep(game, SLEEP_TIME);
        verify(sleepService, times(1)).sleep(100);

        assertThat(result).isEqualTo(underTest);
    }
}