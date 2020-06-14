package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.UsernameValidator;
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
public class ChangeUsernameServiceTest {
    private static final String USERNAME = "username";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final String PASSWORD_HASH = "password-hash";

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserDao userDao;

    @Mock
    private UsernameValidator usernameValidator;

    @InjectMocks
    private ChangeUsernameService underTest;

    @Mock
    private User user;

    @After
    public void check() {
        verify(usernameValidator).validateUsername(USERNAME);
    }

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.changeUsername(USER_ID, ChangeUsernameRequest.builder().username(USERNAME).build()));

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

        Throwable ex = catchThrowable(() -> underTest.changeUsername(USER_ID, ChangeUsernameRequest.builder().username(USERNAME).password(PASSWORD).build()));
        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.BAD_PASSWORD.name());
    }

    @Test
    public void changeUsername() {
        given(userDao.findById(USER_ID)).willReturn(user);
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(true);

        underTest.changeUsername(USER_ID, ChangeUsernameRequest.builder().password(PASSWORD).username(USERNAME).build());

        verify(user).setUsername(USERNAME);
        verify(userDao).save(user);
    }
}