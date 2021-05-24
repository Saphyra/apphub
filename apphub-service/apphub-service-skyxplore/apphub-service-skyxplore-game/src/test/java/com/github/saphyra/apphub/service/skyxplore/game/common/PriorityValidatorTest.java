package com.github.saphyra.apphub.service.skyxplore.game.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;

@RunWith(MockitoJUnitRunner.class)
public class PriorityValidatorTest {
    @InjectMocks
    private PriorityValidator underTest;

    @Test
    public void nullPriority() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("priority", "must not be null");
    }

    @Test
    public void priorityTooLow() {
        Throwable ex = catchThrowable(() -> underTest.validate(0));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("priority", "too low");
    }

    @Test
    public void priorityTooHigh() {
        Throwable ex = catchThrowable(() -> underTest.validate(11));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("priority", "too high");
    }

    @Test
    public void valid() {
        underTest.validate(4);
    }
}