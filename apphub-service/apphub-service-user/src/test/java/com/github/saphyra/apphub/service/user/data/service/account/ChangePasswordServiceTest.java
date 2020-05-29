package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.PasswordValidator;
import com.github.saphyra.encryption.impl.PasswordService;
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
public class ChangePasswordServiceTest {
    private static final String NEW_PASSWORD = "new-password";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD_HASH = "password-hash";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD_HASH = "new-password-hash";

    @Mock
    private PasswordService passwordService;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private ChangePasswordService underTest;

    @Mock
    private User user;

    @After
    public void check() {
        verify(passwordValidator).validatePassword(NEW_PASSWORD);
    }

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.changePassword(USER_ID, ChangePasswordRequest.builder().newPassword(NEW_PASSWORD).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("password")).isEqualTo("must not be null");
    }

    @Test
    public void invalidPassword() {
        given(userDao.findById(USER_ID)).willReturn(user);
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.changePassword(USER_ID, ChangePasswordRequest.builder().newPassword(NEW_PASSWORD).password(PASSWORD).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.BAD_PASSWORD.name());
    }

    @Test
    public void changePassword() {
        given(userDao.findById(USER_ID)).willReturn(user);
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(true);
        given(passwordService.hashPassword(NEW_PASSWORD)).willReturn(NEW_PASSWORD_HASH);

        underTest.changePassword(USER_ID, ChangePasswordRequest.builder().newPassword(NEW_PASSWORD).password(PASSWORD).build());

        verify(user).setPassword(NEW_PASSWORD_HASH);
        verify(userDao).save(user);
    }
}