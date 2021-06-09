package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        ExceptionValidator.validateInvalidParam(ex, "username", "must not be null");
    }

    @Test
    public void tooShortUsername() {
        Throwable ex = catchThrowable(() -> underTest.validateUsername("aa"));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.USERNAME_TOO_SHORT);
    }

    @Test
    public void tooLongUsername() {
        Throwable ex = catchThrowable(() -> underTest.validateUsername(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.USERNAME_TOO_LONG);
    }

    @Test
    public void usernameAlreadyExists() {
        given(userDao.findByUsername(USERNAME)).willReturn(Optional.of(user));

        Throwable ex = catchThrowable(() -> underTest.validateUsername(USERNAME));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    @Test
    public void valid() {
        given(userDao.findByUsername(USERNAME)).willReturn(Optional.empty());

        underTest.validateUsername(USERNAME);
    }
}