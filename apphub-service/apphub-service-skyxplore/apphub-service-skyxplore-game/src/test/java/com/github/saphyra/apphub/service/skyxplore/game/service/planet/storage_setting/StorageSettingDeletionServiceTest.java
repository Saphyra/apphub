package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageSettingDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
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
    private GameData gameData;

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
        given(game.getData()).willReturn(gameData);
        given(syncCacheFactory.create(game)).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);

        given(eventLoop.process(any(Runnable.class), any())).willReturn(future);
        given(future.get()).willReturn(executionResult);

        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(STORAGE_SETTING_ID, ProcessType.STORAGE_SETTING)).willReturn(process);

        underTest.deleteStorageSetting(USER_ID, STORAGE_SETTING_ID);

        verify(eventLoop).process(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(process).cancel(syncCache);
        verify(executionResult).getOrThrow();
    }
}