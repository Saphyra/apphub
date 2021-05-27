package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.request.ChangeEmailRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.EmailValidator;
import org.junit.After;
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
public class ChangeEmailServiceTest {
    private static final String EMAIL = "email";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD_HASH = "password-hash";
    private static final String PASSWORD = "password";

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private ChangeEmailService underTest;

    @Mock
    private User user;

    @After
    public void check() {
        verify(emailValidator).validateEmail(EMAIL);
    }

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.changeEmail(USER_ID, ChangeEmailRequest.builder().email(EMAIL).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("password")).isEqualTo("must not be null");
    }

    @Test
    public void invalidPassword() {
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.changeEmail(USER_ID, ChangeEmailRequest.builder().email(EMAIL).password(PASSWORD).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.BAD_PASSWORD.name());
    }

    @Test
    public void changeEmail() {
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(true);

        underTest.changeEmail(USER_ID, ChangeEmailRequest.builder().email(EMAIL).password(PASSWORD).build());

        verify(user).setEmail(EMAIL);
        verify(userDao).save(user);
    }
}