package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query.StorageSettingsResponseQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageSettingDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private StorageSettingsResponseQueryService storageSettingsResponseQueryService;

    @InjectMocks
    private StorageSettingDeletionService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<List<StorageSettingApiModel>> executionResult;

    @Mock
    private Future<ExecutionResult<List<StorageSettingApiModel>>> future;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private StorageSettingApiModel apiModel;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Captor
    private ArgumentCaptor<Callable<List<StorageSettingApiModel>>> argumentCaptor;

    @BeforeEach
    void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storageSettings.findByStorageSettingIdValidated(STORAGE_SETTING_ID)).willReturn(storageSetting);
        given(gameData.getPlanets()).willReturn(planets);
        given(storageSetting.getLocation()).willReturn(PLANET_ID);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
    }

    @Test
    void forbiddenOperation() {
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.deleteStorageSetting(USER_ID, STORAGE_SETTING_ID));
    }

    @Test
    public void deleteStorageSetting() throws Exception {
        given(game.getEventLoop()).willReturn(eventLoop);

        //noinspection unchecked
        given(eventLoop.processWithResponse(any(Callable.class))).willReturn(future);
        given(future.get()).willReturn(executionResult);

        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndType(STORAGE_SETTING_ID, ProcessType.STORAGE_SETTING)).willReturn(Optional.of(process));
        given(storageSettingsResponseQueryService.getStorageSettings(USER_ID, PLANET_ID)).willReturn(List.of(apiModel));
        given(executionResult.getOrThrow()).willReturn(List.of(apiModel));
        given(game.getProgressDiff()).willReturn(progressDiff);

        List<StorageSettingApiModel> result = underTest.deleteStorageSetting(USER_ID, STORAGE_SETTING_ID);

        verify(eventLoop).processWithResponse(argumentCaptor.capture());
        argumentCaptor.getValue()
            .call();

        verify(process).cleanup();
        verify(executionResult).getOrThrow();
        verify(storageSettings).remove(storageSetting);
        verify(progressDiff).delete(STORAGE_SETTING_ID, GameItemType.STORAGE_SETTING);

        assertThat(result).containsExactly(apiModel);
    }
}