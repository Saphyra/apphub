package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingEditionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final int PRIORITY = 234;
    private static final int BATCH_SIZE = 2344;
    private static final Integer NEW_TARGET_AMOUNT = 2456;

    @Mock
    private GameDao gameDao;

    @Mock
    private StorageSettingsModelValidator storageSettingsModelValidator;

    @InjectMocks
    private StorageSettingEditionService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageSettingModel storageSettingModel;

    @Mock
    private StorageSetting storageSetting;

    @Before
    public void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStorageSettings()).willReturn(storageSettings);

        given(storageSettingModel.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);
    }

    @After
    public void validate() {
        verify(storageSettingsModelValidator).validate(storageSettingModel);
    }

    @Test
    public void storageSettingNotFound() {
        given(storageSettings.findByStorageSettingId(STORAGE_SETTING_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.edit(USER_ID, PLANET_ID, storageSettingModel));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void edit() {
        given(storageSettings.findByStorageSettingId(STORAGE_SETTING_ID)).willReturn(Optional.of(storageSetting));
        given(storageSettingModel.getPriority()).willReturn(PRIORITY);
        given(storageSettingModel.getBatchSize()).willReturn(BATCH_SIZE);
        given(storageSettingModel.getTargetAmount()).willReturn(NEW_TARGET_AMOUNT);

        underTest.edit(USER_ID, PLANET_ID, storageSettingModel);

        verify(storageSetting).setPriority(PRIORITY);
        verify(storageSetting).setBatchSize(BATCH_SIZE);
        verify(storageSetting).setTargetAmount(NEW_TARGET_AMOUNT);
    }
}