package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {
    private static final String USER_IDENTIFIER = "user-identifier";
    private static final String PASSWORD = "password";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private AccessTokenFactory accessTokenFactory;

    @Mock
    private UserDao userDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private LoginService underTest;

    @Mock
    private User user;

    @Mock
    private AccessToken accessToken;

    @Test
    public void userNotFound() {
        given(userDao.findByUsernameOrEmail(USER_IDENTIFIER)).willReturn(Optional.empty());

        LoginRequest request = LoginRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void userLocked() {
        given(userDao.findByUsernameOrEmail(USER_IDENTIFIER)).willReturn(Optional.of(user));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(user.getLockedUntil()).willReturn(CURRENT_TIME.plusSeconds(1));

        LoginRequest request = LoginRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.ACCOUNT_LOCKED);
    }

    @Test
    public void invalidPassword() {
        given(userDao.findByUsernameOrEmail(USER_IDENTIFIER)).willReturn(Optional.of(user));
        given(user.getUserId()).willReturn(USER_ID);
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willThrow(ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.INCORRECT_PASSWORD, ""));

        LoginRequest request = LoginRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS);

        verify(accessTokenProvider).set(AccessTokenHeader.builder().userId(USER_ID).build());
        verify(accessTokenProvider).clear();
    }

    @Test
    public void userMarkedForDeletion() {
        given(userDao.findByUsernameOrEmail(USER_IDENTIFIER)).willReturn(Optional.of(user));
        given(user.isMarkedForDeletion()).willReturn(true);

        LoginRequest request = LoginRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void login() {
        given(userDao.findByUsernameOrEmail(USER_IDENTIFIER)).willReturn(Optional.of(user));
        given(user.getUserId()).willReturn(USER_ID);
        given(accessTokenFactory.create(USER_ID, false)).willReturn(accessToken);

        LoginRequest request = LoginRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .password(PASSWORD)
            .rememberMe(null)
            .build();

        AccessToken result = underTest.login(request);

        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
        verify(accessTokenDao).save(accessToken);
        verify(accessTokenProvider).set(AccessTokenHeader.builder().userId(USER_ID).build());
        verify(accessTokenProvider).clear();

        assertThat(result).isEqualTo(accessToken);
    }
}