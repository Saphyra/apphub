package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
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

        ExceptionValidator.validateInvalidParam(ex, "email", "must not be null");
    }

    @Test
    public void invalidEmail() {
        Throwable ex = catchThrowable(() -> underTest.validateEmail("asd"));

        ExceptionValidator.validateInvalidParam(ex, "email", "invalid format");
    }

    @Test
    public void emailAlreadyExists() {
        given(userDao.findByUsernameOrEmail(EMAIL)).willReturn(Optional.of(user));

        Throwable ex = catchThrowable(() -> underTest.validateEmail(EMAIL));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    public void valid() {
        given(userDao.findByUsernameOrEmail(EMAIL)).willReturn(Optional.empty());

        underTest.validateEmail(EMAIL);
    }
}