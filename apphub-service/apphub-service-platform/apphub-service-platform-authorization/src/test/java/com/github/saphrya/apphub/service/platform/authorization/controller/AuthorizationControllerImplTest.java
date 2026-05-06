package com.github.saphrya.apphub.service.platform.authorization.controller;

import com.github.saphrya.apphub.service.platform.authorization.service.LoginService;
import com.github.saphrya.apphub.service.platform.authorization.service.LogoutService;
import com.github.saphrya.apphub.service.platform.authorization.service.RefreshTokenService;
import com.github.saphyra.apphub.api.platform.authorization.model.LoginRequest;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorizationControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String ACCESS_TOKEN = "access-token";

    @Mock
    private LoginService loginService;

    @Mock
    private LogoutService logoutService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthorizationControllerImpl underTest;

    @Mock
    private LoginRequest loginRequest;

    @Mock
    private TokenResponse tokenResponse;

    @Test
    public void login() {
        given(loginService.login(loginRequest)).willReturn(tokenResponse);

        TokenResponse result = underTest.login(loginRequest);

        assertThat(result).isEqualTo(tokenResponse);
    }

    @Test
    public void logout_blankRefreshToken() {
        underTest.logout(" ", ACCESS_TOKEN);

        then(loginService).shouldHaveNoInteractions();
    }

    @Test
    public void logout() {
        underTest.logout(REFRESH_TOKEN, ACCESS_TOKEN);

        verify(logoutService).logout(REFRESH_TOKEN, ACCESS_TOKEN);
    }

    @Test
    public void refresh_blankRefreshToken() {
        ResponseEntity<TokenResponse> result = underTest.refresh("");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        then(refreshTokenService).shouldHaveNoInteractions();
    }

    @Test
    public void refresh() {
        given(refreshTokenService.refresh(REFRESH_TOKEN)).willReturn(tokenResponse);

        ResponseEntity<TokenResponse> result = underTest.refresh(REFRESH_TOKEN);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(tokenResponse);
    }

    @Test
    public void invalidateAllRefreshTokens() {
        underTest.invalidateAllRefreshTokens(USER_ID);

        then(logoutService).should().invalidateAllRefreshTokens(USER_ID);
    }

    @Test
    public void invalidateAllAccessTokens() {
        underTest.invalidateAllAccessTokens(USER_ID);

        then(logoutService).should().invalidateAllAccessTokens(USER_ID);
    }
}