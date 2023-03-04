package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.StorageSettingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
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
    private Universe universe;

    @Mock
    private Planet planet;

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
    private StorageDetails storageDetails;

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
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);

        given(syncCacheFactory.create()).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithResponse(any(Callable.class), any())).willReturn(future);
        given(future.get()).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(response);

        given(storageSettingFactory.create(request, PLANET_ID, LocationType.PLANET)).willReturn(storageSetting);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStorageSettings()).willReturn(storageSettings);
        given(storageSettingProcessFactory.create(game, planet, storageSetting)).willReturn(process);
        given(process.toModel()).willReturn(processModel);
        given(game.getProcesses()).willReturn(processes);
        given(game.getGameId()).willReturn(GAME_ID);
        given(storageSettingToModelConverter.convert(storageSetting, GAME_ID)).willReturn(model);
        given(storageSettingToApiModelMapper.convert(storageSetting)).willReturn(response);

        StorageSettingApiModel result = underTest.createStorageSetting(USER_ID, PLANET_ID, request);

        verify(storageSettingsModelValidator).validate(request, planet);

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