package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
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
public class StorageSettingEditionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final int PRIORITY = 2;
    private static final int BATCH_SIZE = 245;
    private static final int TARGET_AMOUNT = 2456;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private StorageSettingsModelValidator storageSettingsModelValidator;

    @Mock
    private StorageSettingToModelConverter storageSettingToModelConverter;

    @Mock
    private StorageSettingToApiModelMapper storageSettingToApiModelMapper;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @InjectMocks
    private StorageSettingEditionService underTest;

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
    private StorageSettingModel model;

    @Captor
    private ArgumentCaptor<Callable<StorageSettingApiModel>> argumentCaptor;

    @Test
    public void edit() throws Exception {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(game.getGameId()).willReturn(GAME_ID);
        given(syncCacheFactory.create(game)).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        //noinspection unchecked
        given(eventLoop.processWithResponse(any(Callable.class), any())).willReturn(future);
        given(future.get()).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(response);

        given(request.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);
        given(request.getPriority()).willReturn(PRIORITY);
        given(request.getBatchSize()).willReturn(BATCH_SIZE);
        given(request.getTargetAmount()).willReturn(TARGET_AMOUNT);

        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storageSettings.findByStorageSettingIdValidated(STORAGE_SETTING_ID)).willReturn(storageSetting);
        given(storageSettingToModelConverter.convert(GAME_ID, storageSetting)).willReturn(model);
        given(storageSettingToApiModelMapper.convert(storageSetting)).willReturn(response);

        StorageSettingApiModel result = underTest.edit(USER_ID, request);

        verify(eventLoop).processWithResponse(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .call();

        verify(storageSettingsModelValidator).validate(request);
        verify(storageSetting).setPriority(PRIORITY);
        verify(storageSetting).setBatchSize(BATCH_SIZE);
        verify(storageSetting).setTargetAmount(TARGET_AMOUNT);
        verify(syncCache).saveGameItem(model);
        assertThat(result).isEqualTo(response);
    }
}