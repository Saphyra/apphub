package com.github.saphyra.apphub.service.skyxplore.data.save_game.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StoredResourceModelValidatorTest {
    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private StoredResourceModelValidator underTest;

    @Mock
    private StoredResourceModel model;

    @Before
    public void setUp() {
        given(model.getLocation()).willReturn(UUID.randomUUID());
        given(model.getLocationType()).willReturn("location-type");
        given(model.getDataId()).willReturn("data-id");
        given(model.getAmount()).willReturn(234);
    }

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullLocation() {
        given(model.getLocation()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("location", "must not be null");
    }

    @Test
    public void nullLocationType() {
        given(model.getLocationType()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("locationType", "must not be null");
    }

    @Test
    public void dataId() {
        given(model.getDataId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("dataId", "must not be null");
    }

    @Test
    public void nullAmount() {
        given(model.getAmount()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("amount", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}