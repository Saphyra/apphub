package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshTokenDao;
import com.github.saphrya.apphub.service.platform.authorization.etc.AccessTokenDto;
import com.github.saphrya.apphub.service.platform.authorization.etc.AuthorizationClientProxy;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationRequest;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResponse;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResult;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.api.platform.authorization.model.LoginRequest;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final String USER_IDENTIFIER = "user@example.com";
    private static final String PASSWORD = "password";
    private static final List<Role> ROLES = List.of(Role.TEST);
    private static final String REFRESH_TOKEN_JWT = "refresh-jwt";

    @Mock
    private TokenService tokenService;

    @Mock
    private LoginRequestValidator loginRequestValidator;

    @Mock
    private RefreshTokenDao refreshTokenDao;

    @Mock
    private AuthorizationClientProxy authorizationClientProxy;

    @Mock
    private TokenResponseMapper tokenResponseMapper;

    @InjectMocks
    private LoginService underTest;

    @Mock
    private RefreshToken refreshToken;

    @Mock
    private AccessTokenDto accessTokenDto;

    @Mock
    private TokenResponse tokenResponse;

    @Test
    void login_userNotFound() {
        LoginRequest request = loginRequest();

        given(authorizationClientProxy.authorize(any())).willReturn(AuthorizationResponse.builder()
            .authorizationResult(AuthorizationResult.USER_NOT_FOUND)
            .build());

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    void login_userLocked() {
        LoginRequest request = loginRequest();

        given(authorizationClientProxy.authorize(any())).willReturn(AuthorizationResponse.builder()
            .authorizationResult(AuthorizationResult.USER_LOCKED)
            .userId(USER_ID)
            .build());

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.ACCOUNT_LOCKED);
    }

    @Test
    void login_incorrectPassword() {
        LoginRequest request = loginRequest();

        given(authorizationClientProxy.authorize(any())).willReturn(AuthorizationResponse.builder()
            .authorizationResult(AuthorizationResult.INCORRECT_PASSWORD)
            .userId(USER_ID)
            .build());

        Throwable ex = catchThrowable(() -> underTest.login(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    void login_authorized() {
        LoginRequest request = loginRequest();

        given(authorizationClientProxy.authorize(
            AuthorizationRequest.builder()
                .userIdentifier(USER_IDENTIFIER.toLowerCase())
                .password(PASSWORD)
                .build()
        ))
            .willReturn(AuthorizationResponse.builder()
                .authorizationResult(AuthorizationResult.AUTHORIZED)
                .userId(USER_ID)
                .roles(ROLES)
                .build());

        given(tokenService.createRefreshToken(USER_ID, true)).willReturn(new BiWrapper<>(REFRESH_TOKEN_JWT, refreshToken));
        given(refreshToken.getRefreshTokenId()).willReturn(REFRESH_TOKEN_ID);
        given(tokenService.createAccessToken(USER_ID, REFRESH_TOKEN_ID, ROLES)).willReturn(accessTokenDto);
        given(tokenResponseMapper.create(refreshToken, REFRESH_TOKEN_JWT, accessTokenDto)).willReturn(tokenResponse);

        TokenResponse result = underTest.login(request);

        then(loginRequestValidator).should().validate(request);
        then(refreshTokenDao).should().save(refreshToken);
        assertThat(result).isEqualTo(tokenResponse);
    }

    private LoginRequest loginRequest() {
        return LoginRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .password(PASSWORD)
            .rememberMe(true)
            .build();
    }
}