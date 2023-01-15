package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageSettingModelValidatorTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final String DATA_ID = "data-id";
    private static final Integer TARGET_AMOUNT = 234;
    private static final Integer PRIORITY = 235;
    private static final Integer BATCH_SIZE = 5462;

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private StorageSettingModelValidator underTest;

    @Mock
    private StorageSettingModel model;

    @BeforeEach
    public void setUp() {
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getLocationType()).willReturn(LOCATION_TYPE);
        given(model.getDataId()).willReturn(DATA_ID);
        given(model.getTargetAmount()).willReturn(TARGET_AMOUNT);
        given(model.getPriority()).willReturn(PRIORITY);
        given(model.getBatchSize()).willReturn(BATCH_SIZE);
    }

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullLocation() {
        given(model.getLocation()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "location", "must not be null");
    }

    @Test
    public void nullLocationType() {
        given(model.getLocationType()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "locationType", "must not be null");
    }

    @Test
    public void nullDataId() {
        given(model.getDataId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "dataId", "must not be null");
    }

    @Test
    public void nullTargetAmount() {
        given(model.getTargetAmount()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "targetAmount", "must not be null");
    }

    @Test
    public void nullPriority() {
        given(model.getPriority()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "priority", "must not be null");
    }

    @Test
    public void nullBatchSize() {
        given(model.getBatchSize()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "batchSize", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}