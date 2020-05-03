package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.data.client.UserDataApiClient;
import com.github.saphyra.apphub.api.user.data.model.response.InternalUserResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.exception.UnauthorizedException;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.encryption.impl.PasswordService;
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
public class LoginServiceTest {
    private static final String EMAIL = "email";
    private static final String PASSWORD_HASH = "password-hash";
    private static final String PASSWORD = "password";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private AccessTokenFactory accessTokenFactory;

    @Mock
    private UserDataApiClient internalUserDataApi;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private LoginService underTest;

    @Mock
    private InternalUserResponse userResponse;

    @Mock
    private AccessToken accessToken;

    @Test
    public void userNotFound() {
        given(internalUserDataApi.findByEmail(EMAIL)).willThrow(new RuntimeException());

        LoginRequest request = LoginRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        assertThat(ex).isInstanceOf(UnauthorizedException.class);
        UnauthorizedException exception = (UnauthorizedException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
    }

    @Test
    public void invalidPassword() {
        given(internalUserDataApi.findByEmail(EMAIL)).willReturn(userResponse);
        given(userResponse.getPasswordHash()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(false);

        LoginRequest request = LoginRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.login(request));

        assertThat(ex).isInstanceOf(UnauthorizedException.class);
        UnauthorizedException exception = (UnauthorizedException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
    }

    @Test
    public void login() {
        given(internalUserDataApi.findByEmail(EMAIL)).willReturn(userResponse);
        given(userResponse.getUserId()).willReturn(USER_ID);
        given(userResponse.getPasswordHash()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(true);
        given(accessTokenFactory.create(USER_ID, false)).willReturn(accessToken);

        LoginRequest request = LoginRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .rememberMe(null)
            .build();

        AccessToken result = underTest.login(request);

        verify(accessTokenDao).save(accessToken);
        assertThat(result).isEqualTo(accessToken);
    }
}