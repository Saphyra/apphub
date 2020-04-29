package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.user.data.model.request.RegistrationRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import com.github.saphyra.apphub.lib.error_handler.exception.ConflictException;
import com.github.saphyra.apphub.service.user.data.dao.User;
import com.github.saphyra.apphub.service.user.data.dao.UserDao;
import org.junit.Before;
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
public class RegistrationRequestValidatorTest {
    private static final String VALID_EMAIL = "asd@asd.asd";
    private static final String VALID_USERNAME = "valid-username";
    private static final String VALID_PASSWORD = "valid-password";
    private static final String INVALID_EMAIL = "invalid-email";
    private static final String TOO_SHORT_USERNAME = "to";
    private static final String TOO_LONG_USERNAME = Stream.generate(() -> "a")
        .limit(31)
        .collect(Collectors.joining());
    private static final String TOO_LONG_PASSWORD = Stream.generate(() -> "a")
        .limit(31)
        .collect(Collectors.joining());

    @Mock
    private UserDao userDao;

    @InjectMocks
    private RegistrationRequestValidator underTest;

    private RegistrationRequest.RegistrationRequestBuilder builder;

    @Mock
    private User user;

    @Before
    public void setUp() {
        given(userDao.findByEmail(VALID_EMAIL)).willReturn(Optional.empty());
        given(userDao.findByUsername(VALID_USERNAME)).willReturn(Optional.empty());

        builder = RegistrationRequest.builder()
            .username(VALID_USERNAME)
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD);
    }

    @Test
    public void nullEmail() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.email(null).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("email")).isEqualTo("must not be null");
    }

    @Test
    public void invalidEmail() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.email(INVALID_EMAIL).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("email")).isEqualTo("invalid format");
    }

    @Test
    public void emailAlreadyExists() {
        given(userDao.findByEmail(VALID_EMAIL)).willReturn(Optional.of(user));

        Throwable ex = catchThrowable(() -> underTest.validate(builder.build()));

        assertThat(ex).isInstanceOf(ConflictException.class);
        ConflictException exception = (ConflictException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.name());
    }

    @Test
    public void nullUsername() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.username(null).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("username")).isEqualTo("must not be null");
    }

    @Test
    public void tooShortUsername() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.username(TOO_SHORT_USERNAME).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_SHORT.name());
    }

    @Test
    public void tooLongUsername() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.username(TOO_LONG_USERNAME).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_LONG.name());

    }

    @Test
    public void usernameAlreadyExists() {
        given(userDao.findByUsername(VALID_USERNAME)).willReturn(Optional.of(user));

        Throwable ex = catchThrowable(() -> underTest.validate(builder.build()));

        assertThat(ex).isInstanceOf(ConflictException.class);
        ConflictException exception = (ConflictException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.USERNAME_ALREADY_EXISTS.name());
    }

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.password(null).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("password")).isEqualTo("must not be null");
    }

    @Test
    public void tooShortPassword() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.password("aaaaa").build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.PASSWORD_TOO_SHORT.name());
    }

    @Test
    public void tooLongPassword() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.password(TOO_LONG_PASSWORD).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.PASSWORD_TOO_LONG.name());
    }

    @Test
    public void valid() {
        underTest.validate(builder.build());
    }
}