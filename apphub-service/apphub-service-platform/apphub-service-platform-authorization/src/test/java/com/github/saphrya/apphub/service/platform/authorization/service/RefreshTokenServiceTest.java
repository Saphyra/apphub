package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshTokenDao;
import com.github.saphrya.apphub.service.platform.authorization.etc.AccessTokenDto;
import com.github.saphrya.apphub.service.platform.authorization.etc.AuthorizationClientProxy;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final UUID NEW_REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final String REFRESH_TOKEN_STRING = "refresh-token";
    private static final String NEW_REFRESH_TOKEN_JWT = "new-refresh-jwt";
    private static final List<String> ROLES = List.of("ROLE_A");

    @Mock
    private TokenService tokenService;

    @Mock
    private RefreshTokenDao refreshTokenDao;

    @Mock
    private AuthorizationClientProxy authorizationClientProxy;

    @Mock
    private TokenResponseMapper tokenResponseMapper;

    @InjectMocks
    private RefreshTokenService underTest;

    @Mock
    private RefreshToken parsedToken;

    @Mock
    private RefreshToken newRefreshToken;

    @Mock
    private AccessTokenDto accessTokenDto;

    @Mock
    private TokenResponse tokenResponse;

    @Test
    void refresh_sessionNotFound() {
        given(tokenService.verifyRefreshToken(REFRESH_TOKEN_STRING)).willReturn(parsedToken);
        given(parsedToken.getUserId()).willReturn(USER_ID);
        given(parsedToken.getRefreshTokenId()).willReturn(REFRESH_TOKEN_ID);
        given(refreshTokenDao.findByUserIdAndRefreshTokenId(USER_ID, REFRESH_TOKEN_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.refresh(REFRESH_TOKEN_STRING));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE);
    }

    @Test
    void refresh() {
        given(tokenService.verifyRefreshToken(REFRESH_TOKEN_STRING)).willReturn(parsedToken);
        given(parsedToken.getUserId()).willReturn(USER_ID);
        given(parsedToken.getRefreshTokenId()).willReturn(REFRESH_TOKEN_ID);
        given(parsedToken.isRememberMe()).willReturn(true);
        given(refreshTokenDao.findByUserIdAndRefreshTokenId(USER_ID, REFRESH_TOKEN_ID)).willReturn(Optional.of(parsedToken));

        given(tokenService.createRefreshToken(USER_ID, true)).willReturn(new BiWrapper<>(NEW_REFRESH_TOKEN_JWT, newRefreshToken));
        given(newRefreshToken.getRefreshTokenId()).willReturn(NEW_REFRESH_TOKEN_ID);
        given(authorizationClientProxy.getRoles(USER_ID)).willReturn(ROLES);
        given(tokenService.createAccessToken(USER_ID, NEW_REFRESH_TOKEN_ID, ROLES)).willReturn(accessTokenDto);
        given(tokenResponseMapper.create(newRefreshToken, NEW_REFRESH_TOKEN_JWT, accessTokenDto)).willReturn(tokenResponse);

        TokenResponse result = underTest.refresh(REFRESH_TOKEN_STRING);

        then(refreshTokenDao).should().delete(USER_ID, REFRESH_TOKEN_ID);
        then(refreshTokenDao).should().save(newRefreshToken);
        assertThat(result).isEqualTo(tokenResponse);
    }
}