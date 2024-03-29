package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageSettingsModelValidatorTest {
    private static final Integer PRIORITY = 325;
    private static final String DATA_ID = "data-id";
    private static final Integer TARGET_AMOUNT = 42362;
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private PriorityValidator priorityValidator;

    @InjectMocks
    private StorageSettingsModelValidator underTest;

    @Mock
    private StorageSettingApiModel model;

    @Mock
    private Planet planet;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private GameData gameData;

    @AfterEach
    public void validate() {
        verify(priorityValidator).validate(PRIORITY);
    }

    @Test
    public void blankDataId() {
        given(model.getPriority()).willReturn(PRIORITY);

        given(model.getDataId()).willReturn(" ");

        Throwable ex = catchThrowable(() -> underTest.validate(gameData, LOCATION, model));

        ExceptionValidator.validateInvalidParam(ex, "dataId", "must not be null or blank");
    }

    @Test
    public void unknownDataId() {
        given(model.getPriority()).willReturn(PRIORITY);
        given(model.getDataId()).willReturn(DATA_ID);

        given(resourceDataService.containsKey(DATA_ID)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.validate(gameData, LOCATION, model));

        ExceptionValidator.validateInvalidParam(ex, "dataId", "unknown resource");
    }

    @Test
    public void nullTargetAmount() {
        given(model.getPriority()).willReturn(PRIORITY);
        given(model.getDataId()).willReturn(DATA_ID);
        given(resourceDataService.containsKey(DATA_ID)).willReturn(true);

        given(model.getTargetAmount()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(gameData, LOCATION, model));

        ExceptionValidator.validateInvalidParam(ex, "targetAmount", "must not be null");
    }

    @Test
    public void tooLowTargetAmount() {
        given(model.getPriority()).willReturn(PRIORITY);
        given(model.getDataId()).willReturn(DATA_ID);
        given(resourceDataService.containsKey(DATA_ID)).willReturn(true);

        given(model.getTargetAmount()).willReturn(-1);

        Throwable ex = catchThrowable(() -> underTest.validate(gameData, LOCATION, model));

        ExceptionValidator.validateInvalidParam(ex, "targetAmount", "too low");
    }

    @Test
    public void settingAlreadyExists() {
        given(model.getPriority()).willReturn(PRIORITY);
        given(model.getDataId()).willReturn(DATA_ID);
        given(resourceDataService.containsKey(DATA_ID)).willReturn(true);
        given(model.getTargetAmount()).willReturn(TARGET_AMOUNT);

        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storageSettings.findByLocationAndDataId(LOCATION, DATA_ID)).willReturn(Optional.of(storageSetting));

        Throwable ex = catchThrowable(() -> underTest.validate(gameData, LOCATION, model));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void valid() {
        given(model.getPriority()).willReturn(PRIORITY);
        given(model.getDataId()).willReturn(DATA_ID);
        given(resourceDataService.containsKey(DATA_ID)).willReturn(true);
        given(model.getTargetAmount()).willReturn(TARGET_AMOUNT);

        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storageSettings.findByLocationAndDataId(LOCATION, DATA_ID)).willReturn(Optional.empty());

        underTest.validate(gameData, LOCATION, model);
    }
}