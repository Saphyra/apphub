package com.github.saphyra.apphub.service.skyxplore.data.save_game.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
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
public class ReservedStorageModelValidatorTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 2543;

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private ReservedStorageModelValidator underTest;

    @Mock
    private ReservedStorageModel model;

    @Before
    public void setUp() {
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getDataId()).willReturn(DATA_ID);
        given(model.getAmount()).willReturn(AMOUNT);
    }

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullExternalReference() {
        given(model.getExternalReference()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("externalReference", "must not be null");
    }

    @Test
    public void nullDataId() {
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