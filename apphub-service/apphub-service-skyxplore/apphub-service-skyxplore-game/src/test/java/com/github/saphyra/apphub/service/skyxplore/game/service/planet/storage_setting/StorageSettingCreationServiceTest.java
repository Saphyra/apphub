package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.StorageSettingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettingConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query.StorageSettingsResponseQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
    private StorageSettingConverter storageSettingConverter;

    @Mock
    private StorageSettingsResponseQueryService storageSettingsResponseQueryService;

    @InjectMocks
    private StorageSettingCreationService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private StorageSettingApiModel request;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<List<StorageSettingApiModel>> executionResult;

    @Mock
    private FutureWrapper<List<StorageSettingApiModel>> future;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private StorageSettingModel model;

    @Mock
    private StorageSettingApiModel apiModel;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Captor
    private ArgumentCaptor<Callable<List<StorageSettingApiModel>>> argumentCaptor;

    @Test
    void forbiddenOperation(){
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.createStorageSetting(USER_ID, PLANET_ID, request));

        then(storageSettingsModelValidator).should().validate(gameData, PLANET_ID, request);
    }

    @Test
    public void create() throws Exception {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);

        given(game.getEventLoop()).willReturn(eventLoop);
        //noinspection unchecked
        given(eventLoop.processWithResponse(any(Callable.class))).willReturn(future);
        given(future.get()).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(List.of(apiModel));

        given(storageSettingFactory.create(request, PLANET_ID)).willReturn(storageSetting);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(game.getGameId()).willReturn(GAME_ID);
        given(storageSettingConverter.toModel(GAME_ID, storageSetting)).willReturn(model);
        given(storageSettingsResponseQueryService.getStorageSettings(USER_ID, PLANET_ID)).willReturn(List.of(apiModel));
        given(game.getProgressDiff()).willReturn(progressDiff);

        List<StorageSettingApiModel> result = underTest.createStorageSetting(USER_ID, PLANET_ID, request);

        verify(storageSettingsModelValidator).validate(gameData, PLANET_ID, request);

        verify(eventLoop).processWithResponse(argumentCaptor.capture());
        argumentCaptor.getValue()
            .call();

        verify(storageSettings).add(storageSetting);
        verify(progressDiff).save(model);
        assertThat(result).containsExactly(apiModel);
    }
}