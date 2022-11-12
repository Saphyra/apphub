package com.github.saphyra.apphub.service.user.common;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.user.authentication.service.LogoutService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CheckPasswordServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD_HASH = "password-hash";
    private static final String PASSWORD = "password";
    private static final Integer LOGIN_FAILURE_COUNT = 24;
    private static final Integer LOCK_ACCOUNT_FAILURES = 256;
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Integer LOCKED_MINUTES = 36;
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordProperties passwordProperties;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private LogoutService logoutService;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private CheckPasswordService underTest;

    @Mock
    private User user;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Before
    public void setUp() {
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(passwordProperties.getLockAccountFailures()).willReturn(LOCK_ACCOUNT_FAILURES);
    }

    @Test
    public void incorrectPassword() {
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(false);
        given(user.getPasswordFailureCount()).willReturn(LOGIN_FAILURE_COUNT);

        Throwable ex = catchThrowable(() -> underTest.checkPassword(USER_ID, PASSWORD));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.INCORRECT_PASSWORD);

        verify(user).setPasswordFailureCount(LOGIN_FAILURE_COUNT + 1);
        verify(userDao).save(user);

        verifyNoInteractions(logoutService);
    }

    @Test
    public void incorrectPassword_lockUser() {
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(false);
        given(user.getPasswordFailureCount()).willReturn(LOCK_ACCOUNT_FAILURES);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(passwordProperties.getLockedMinutes()).willReturn(LOCKED_MINUTES);
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);

        Throwable ex = catchThrowable(() -> underTest.checkPassword(USER_ID, PASSWORD));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.ACCOUNT_LOCKED);

        verify(user).setPasswordFailureCount(LOCK_ACCOUNT_FAILURES + 1);
        verify(user).setLockedUntil(CURRENT_TIME.plusMinutes(LOCKED_MINUTES));
        verify(userDao).save(user);

        verify(logoutService).logout(ACCESS_TOKEN_ID, USER_ID);
    }

    @Test
    public void correctPassword() {
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(true);

        User result = underTest.checkPassword(USER_ID, PASSWORD);

        verify(user).setPasswordFailureCount(0);
        verify(userDao).save(user);

        assertThat(result).isEqualTo(user);
    }
}