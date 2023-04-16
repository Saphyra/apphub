package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.StorageSettingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.storage_setting.StorageSettingProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.storage_setting.StorageSettingProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StorageSettingToModelConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageSettingCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private StorageSettingsModelValidator storageSettingsModelValidator;

    @Mock
    private StorageSettingFactory storageSettingFactory;

    @Mock
    private StorageSettingToModelConverter storageSettingToModelConverter;

    @Mock
    private StorageSettingToApiModelMapper storageSettingToApiModelMapper;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock
    private StorageSettingProcessFactory storageSettingProcessFactory;

    @InjectMocks
    private StorageSettingCreationService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private StorageSettingApiModel request;

    @Mock
    private StorageSettingApiModel response;

    @Mock
    private SyncCache syncCache;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<StorageSettingApiModel> executionResult;

    @Mock
    private Future<ExecutionResult<StorageSettingApiModel>> future;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private ProcessModel processModel;

    @Mock
    private StorageSettingProcess process;

    @Mock
    private Processes processes;

    @Mock
    private StorageSettingModel model;

    @Captor
    private ArgumentCaptor<Callable<StorageSettingApiModel>> argumentCaptor;

    @Test
    public void create() throws Exception {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);

        given(syncCacheFactory.create(game)).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        //noinspection unchecked
        given(eventLoop.processWithResponse(any(Callable.class), any())).willReturn(future);
        given(future.get()).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(response);

        given(storageSettingFactory.create(request, PLANET_ID)).willReturn(storageSetting);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storageSettingProcessFactory.create(gameData, PLANET_ID, storageSetting)).willReturn(process);
        given(process.toModel()).willReturn(processModel);
        given(gameData.getProcesses()).willReturn(processes);
        given(game.getGameId()).willReturn(GAME_ID);
        given(storageSettingToModelConverter.convert(GAME_ID, storageSetting)).willReturn(model);
        given(storageSettingToApiModelMapper.convert(storageSetting)).willReturn(response);

        StorageSettingApiModel result = underTest.createStorageSetting(USER_ID, PLANET_ID, request);

        verify(storageSettingsModelValidator).validate(gameData, PLANET_ID, request);

        verify(eventLoop).processWithResponse(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .call();

        verify(storageSettings).add(storageSetting);
        verify(syncCache).saveGameItem(processModel);
        verify(syncCache).saveGameItem(model);
        verify(processes).add(process);
        assertThat(result).isEqualTo(response);
    }
}