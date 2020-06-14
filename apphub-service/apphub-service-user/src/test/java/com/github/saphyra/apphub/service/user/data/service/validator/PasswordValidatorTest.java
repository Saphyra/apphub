package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class PasswordValidatorTest {
    @InjectMocks
    private PasswordValidator underTest;

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.validatePassword(null));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("password")).isEqualTo("must not be null");
    }

    @Test
    public void tooShortPassword() {
        Throwable ex = catchThrowable(() -> underTest.validatePassword("aaaaa"));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.PASSWORD_TOO_SHORT.name());
    }

    @Test
    public void tooLongPassword() {
        Throwable ex = catchThrowable(() -> underTest.validatePassword(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.PASSWORD_TOO_LONG.name());
    }

    @Test
    public void valid() {
        underTest.validatePassword("asdasd");
    }
}