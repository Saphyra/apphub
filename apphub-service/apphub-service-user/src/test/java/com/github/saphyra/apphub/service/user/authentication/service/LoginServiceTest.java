package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.common.PasswordProperties;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceTest {
    private static final String EMAIL = "email";
    private static final String PASSWORD_HASH = "password-hash";
    private static final String PASSWORD = "password";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Integer LOGIN_FAILURE_COUNT = 1;
    private static final Integer LOCK_ACCOUNT_FAILURES = 254;
    private static final Integer LOCK_MINUTES = 3;

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private AccessTokenFactory accessTokenFactory;

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordService passwordService;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private PasswordProperties passwordProperties;

    @InjectMocks
    private LoginService underTest;

    @Mock
    private User user;

    @Mock
    private AccessToken accessToken;

    @Test
    public void userNotFound() {
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.empty());

        LoginRequest request = LoginRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void userLocked() {
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.of(user));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(user.getLockedUntil()).willReturn(CURRENT_TIME.plusSeconds(1));

        LoginRequest request = LoginRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.ACCOUNT_LOCKED);
    }

    @Test
    public void invalidPassword() {
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.of(user));
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(false);
        given(user.getPasswordFailureCount()).willReturn(LOGIN_FAILURE_COUNT);
        given(passwordProperties.getLockAccountFailures()).willReturn(LOCK_ACCOUNT_FAILURES);

        LoginRequest request = LoginRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        verify(user).setPasswordFailureCount(LOGIN_FAILURE_COUNT + 1);
        verify(user, times(0)).setLockedUntil(any());
        verify(userDao).save(user);

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void invalidPassword_lockAccount() {
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.of(user));
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(false);
        given(user.getPasswordFailureCount()).willReturn(LOCK_ACCOUNT_FAILURES);
        given(passwordProperties.getLockAccountFailures()).willReturn(LOCK_ACCOUNT_FAILURES);
        given(passwordProperties.getLockedMinutes()).willReturn(LOCK_MINUTES);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        LoginRequest request = LoginRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS);

        verify(user).setPasswordFailureCount(LOCK_ACCOUNT_FAILURES + 1);
        verify(user).setLockedUntil(CURRENT_TIME.plusMinutes(LOCK_MINUTES));
        verify(userDao).save(user);
    }

    @Test
    public void userMarkedForDeletion() {
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.of(user));
        given(user.isMarkedForDeletion()).willReturn(true);

        LoginRequest request = LoginRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void login() {
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.of(user));
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(user.getUserId()).willReturn(USER_ID);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(true);
        given(accessTokenFactory.create(USER_ID, false)).willReturn(accessToken);

        LoginRequest request = LoginRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .rememberMe(null)
            .build();

        AccessToken result = underTest.login(request);

        verify(user).setPasswordFailureCount(0);
        verify(userDao).save(user);
        verify(accessTokenDao).save(accessToken);
        assertThat(result).isEqualTo(accessToken);
    }
}