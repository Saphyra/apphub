package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @InjectMocks
    private StorageSettingDeletionService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private SyncCache syncCache;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private Future<ExecutionResult<Void>> future;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Captor
    private ArgumentCaptor<Runnable> argumentCaptor;

    @Test
    public void deleteStorageSetting() throws Exception {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(syncCacheFactory.create()).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);

        given(eventLoop.process(any(Runnable.class), any())).willReturn(future);
        given(future.get()).willReturn(executionResult);

        given(game.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(STORAGE_SETTING_ID, ProcessType.STORAGE_SETTING)).willReturn(process);

        underTest.deleteStorageSetting(USER_ID, PLANET_ID, STORAGE_SETTING_ID);

        verify(eventLoop).process(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(process).cancel(syncCache);
        verify(executionResult).getOrThrow();
    }
}