package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingsModelValidatorTest {
    private static final Integer PRIORITY = 325;
    private static final String DATA_ID = "data-id";
    private static final Integer TARGET_AMOUNT = 42362;
    private static final Integer BATCH_SIZE = 425;

    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private PriorityValidator priorityValidator;

    @InjectMocks
    private StorageSettingsModelValidator underTest;

    @Mock
    private StorageSettingModel model;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageSetting storageSetting;

    @Before
    public void setUp() {
        given(model.getPriority()).willReturn(PRIORITY);
        given(model.getDataId()).willReturn(DATA_ID);
        given(resourceDataService.containsKey(DATA_ID)).willReturn(true);
        given(model.getTargetAmount()).willReturn(TARGET_AMOUNT);
        given(model.getBatchSize()).willReturn(BATCH_SIZE);

        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStorageSettings()).willReturn(storageSettings);
    }

    @After
    public void validate() {
        verify(priorityValidator).validate(PRIORITY);
    }

    @Test
    public void blankDataId() {
        given(model.getDataId()).willReturn(" ");

        Throwable ex = catchThrowable(() -> underTest.validate(model, planet));

        ExceptionValidator.validateInvalidParam(ex, "dataId", "must not be null or blank");
    }

    @Test
    public void unknownDataId() {
        given(resourceDataService.containsKey(DATA_ID)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.validate(model, planet));

        ExceptionValidator.validateInvalidParam(ex, "dataId", "unknown resource");
    }

    @Test
    public void nullTargetAmount() {
        given(model.getTargetAmount()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model, planet));

        ExceptionValidator.validateInvalidParam(ex, "targetAmount", "must not be null");
    }

    @Test
    public void tooLowTargetAmount() {
        given(model.getTargetAmount()).willReturn(-1);

        Throwable ex = catchThrowable(() -> underTest.validate(model, planet));

        ExceptionValidator.validateInvalidParam(ex, "targetAmount", "too low");
    }

    @Test
    public void nullBatchSize() {
        given(model.getBatchSize()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model, planet));

        ExceptionValidator.validateInvalidParam(ex, "batchSize", "must not be null");
    }

    @Test
    public void tooLowBatchSize() {
        given(model.getBatchSize()).willReturn(0);

        Throwable ex = catchThrowable(() -> underTest.validate(model, planet));

        ExceptionValidator.validateInvalidParam(ex, "batchSize", "too low");
    }

    @Test
    public void settingAlreadyExists() {
        given(storageSettings.findByDataId(DATA_ID)).willReturn(Optional.of(storageSetting));

        Throwable ex = catchThrowable(() -> underTest.validate(model, planet));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void valid() {
        underTest.validate(model, planet);
    }
}