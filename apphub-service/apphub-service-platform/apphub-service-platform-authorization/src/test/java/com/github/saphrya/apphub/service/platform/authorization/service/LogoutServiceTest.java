package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshTokenDao;
import com.github.saphyra.apphub.api.platform.main_gateway.client.MainGatewayClient;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String REFRESH_TOKEN_STRING = "refresh-token";
    private static final String ACCESS_TOKEN_STRING = "access-token";

    @Mock
    private TokenService tokenService;

    @Mock
    private RefreshTokenDao refreshTokenDao;

    @Mock
    private MainGatewayClient mainGatewayClient;

    @InjectMocks
    private LogoutService underTest;

    @Mock
    private RefreshToken parsedRefreshToken;

    @Mock
    private AccessToken parsedAccessToken;

    @Test
    void logout_withoutAccessToken() {
        given(tokenService.verifyRefreshToken(REFRESH_TOKEN_STRING)).willReturn(parsedRefreshToken);
        given(parsedRefreshToken.getUserId()).willReturn(USER_ID);
        given(parsedRefreshToken.getRefreshTokenId()).willReturn(REFRESH_TOKEN_ID);

        underTest.logout(REFRESH_TOKEN_STRING, "  ");

        then(refreshTokenDao).should().delete(USER_ID, REFRESH_TOKEN_ID);
        then(mainGatewayClient).should().invalidateRefreshTokens(List.of(REFRESH_TOKEN_ID));
        then(mainGatewayClient).shouldHaveNoMoreInteractions();
    }

    @Test
    void logout_withAccessToken() {
        given(tokenService.verifyRefreshToken(REFRESH_TOKEN_STRING)).willReturn(parsedRefreshToken);
        given(parsedRefreshToken.getUserId()).willReturn(USER_ID);
        given(parsedRefreshToken.getRefreshTokenId()).willReturn(REFRESH_TOKEN_ID);
        given(tokenService.parseAccessToken(ACCESS_TOKEN_STRING)).willReturn(Optional.of(parsedAccessToken));
        given(parsedAccessToken.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);

        underTest.logout(REFRESH_TOKEN_STRING, ACCESS_TOKEN_STRING);

        then(refreshTokenDao).should().delete(USER_ID, REFRESH_TOKEN_ID);
        then(mainGatewayClient).should().invalidateRefreshTokens(List.of(REFRESH_TOKEN_ID));
        then(mainGatewayClient).should().invalidateAccessToken(ACCESS_TOKEN_ID);
    }

    @Test
    void invalidateAllRefreshTokens() {
        List<UUID> tokenIds = List.of(REFRESH_TOKEN_ID);
        given(refreshTokenDao.deleteByUserId(USER_ID)).willReturn(tokenIds);

        underTest.invalidateAllRefreshTokens(USER_ID);

        then(mainGatewayClient).should().invalidateRefreshTokens(tokenIds);
    }

    @Test
    void invalidateAllAccessTokens() {
        UUID anotherTokenId = UUID.randomUUID();
        RefreshToken token1 = RefreshToken.builder()
            .userId(USER_ID)
            .refreshTokenId(REFRESH_TOKEN_ID)
            .issuedAt(LocalDateTime.now())
            .expiration(LocalDateTime.now().plusHours(1))
            .build();
        RefreshToken token2 = RefreshToken.builder()
            .userId(USER_ID)
            .refreshTokenId(anotherTokenId)
            .issuedAt(LocalDateTime.now())
            .expiration(LocalDateTime.now().plusHours(1))
            .build();

        given(refreshTokenDao.getByUserId(USER_ID)).willReturn(List.of(token1, token2));

        underTest.invalidateAllAccessTokens(USER_ID);

        then(mainGatewayClient).should().invalidateRefreshTokens(List.of(REFRESH_TOKEN_ID, anotherTokenId));
    }
}