package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import com.github.saphyra.apphub.lib.error_handler.exception.ConflictException;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class EmailValidatorTest {
    private static final String EMAIL = "asd@asd.asd";

    @Mock
    private UserDao userDao;

    @InjectMocks
    private EmailValidator underTest;

    @Mock
    private User user;

    @Test
    public void nullEmail() {
        Throwable ex = catchThrowable(() -> underTest.validateEmail(null));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException)ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("email")).isEqualTo("must not be null");
    }

    @Test
    public void invalidEmail() {
        Throwable ex = catchThrowable(() -> underTest.validateEmail("asd"));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException)ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("email")).isEqualTo("invalid format");
    }

    @Test
    public void emailAlreadyExists() {
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.of(user));

        Throwable ex = catchThrowable(() -> underTest.validateEmail(EMAIL));

        assertThat(ex).isInstanceOf(ConflictException.class);
        ConflictException exception = (ConflictException)ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.name());
    }

    @Test
    public void valid() {
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.empty());

        underTest.validateEmail(EMAIL);
    }
}