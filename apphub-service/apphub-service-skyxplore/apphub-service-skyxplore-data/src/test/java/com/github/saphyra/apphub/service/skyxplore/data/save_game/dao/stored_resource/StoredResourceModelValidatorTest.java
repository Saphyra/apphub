package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
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
public class StoredResourceModelValidatorTest {
    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private StoredResourceModelValidator underTest;

    @Mock
    private StoredResourceModel model;


    @AfterEach
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
        given(model.getLocation()).willReturn(UUID.randomUUID());
        given(model.getLocationType()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "locationType", "must not be null");
    }

    @Test
    public void dataId() {
        given(model.getLocation()).willReturn(UUID.randomUUID());
        given(model.getLocationType()).willReturn("location-type");
        given(model.getDataId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "dataId", "must not be null");
    }

    @Test
    public void nullAmount() {
        given(model.getLocation()).willReturn(UUID.randomUUID());
        given(model.getLocationType()).willReturn("location-type");
        given(model.getDataId()).willReturn("data-id");
        given(model.getAmount()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "amount", "must not be null");
    }

    @Test
    public void valid() {
        given(model.getLocation()).willReturn(UUID.randomUUID());
        given(model.getLocationType()).willReturn("location-type");
        given(model.getDataId()).willReturn("data-id");
        given(model.getAmount()).willReturn(234);

        underTest.validate(model);
    }
}