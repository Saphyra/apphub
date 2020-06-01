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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UsernameValidatorTest {
    private static final String USERNAME = "username";

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UsernameValidator underTest;

    @Mock
    private User user;

    @Test
    public void nullUsername() {
        Throwable ex = catchThrowable(() -> underTest.validateUsername(null));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("username")).isEqualTo("must not be null");
    }

    @Test
    public void tooShortUsername() {
        Throwable ex = catchThrowable(() -> underTest.validateUsername("aa"));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_SHORT.name());
    }

    @Test
    public void tooLongUsername() {
        Throwable ex = catchThrowable(() -> underTest.validateUsername(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_LONG.name());
    }

    @Test
    public void usernameAlreadyExists() {
        given(userDao.findByUsername(USERNAME)).willReturn(Optional.of(user));

        Throwable ex = catchThrowable(() -> underTest.validateUsername(USERNAME));

        assertThat(ex).isInstanceOf(ConflictException.class);
        ConflictException exception = (ConflictException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.USERNAME_ALREADY_EXISTS.name());
    }

    @Test
    public void valid() {
        given(userDao.findByUsername(USERNAME)).willReturn(Optional.empty());

        underTest.validateUsername(USERNAME);
    }
}