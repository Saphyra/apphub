package com.github.saphyra.apphub.service.platform.main_gateway.controller;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedAccessTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedRefreshTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.TokenParser;
import com.github.saphyra.apphub.service.platform.main_gateway.util.ErrorLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MainGatewayRestControllerImplTest {
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ACCESS_TOKEN_STRING = "access-token-string";

    @Mock
    private InvalidatedAccessTokenService invalidatedAccessTokenService;

    @Mock
    private InvalidatedRefreshTokenService invalidatedRefreshTokenService;

    @Mock
    private ErrorLogger errorLogger;

    @Mock
    private TokenParser tokenParser;

    @InjectMocks
    private MainGatewayRestControllerImpl underTest;

    @Test
    void invalidateAccessToken() {
        underTest.invalidateAccessToken(ACCESS_TOKEN_ID).block();

        then(invalidatedAccessTokenService).should().add(ACCESS_TOKEN_ID);
    }

    @Test
    void invalidateRefreshTokens() {

        underTest.invalidateRefreshTokens(List.of(REFRESH_TOKEN_ID)).block();

        then(invalidatedRefreshTokenService).should().add(REFRESH_TOKEN_ID);
    }

    @Test
    void invalidateAccessToken_byString_accessTokenAlreadyInvalidated() {
        AccessToken accessToken = accessToken();
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING)).willReturn(Mono.just(accessToken));
        given(invalidatedAccessTokenService.contains(ACCESS_TOKEN_ID)).willReturn(true);

        ResponseEntity<Object> result = underTest.invalidateAccessToken(ACCESS_TOKEN_STRING).block();

        assertThat(result).isNull();
        then(invalidatedAccessTokenService).shouldHaveNoMoreInteractions();
    }

    @Test
    void invalidateAccessToken_byString_refreshTokenAlreadyInvalidated() {
        AccessToken accessToken = accessToken();
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING)).willReturn(Mono.just(accessToken));
        given(invalidatedAccessTokenService.contains(ACCESS_TOKEN_ID)).willReturn(false);
        given(invalidatedRefreshTokenService.contains(REFRESH_TOKEN_ID)).willReturn(true);

        ResponseEntity<Object> result = underTest.invalidateAccessToken(ACCESS_TOKEN_STRING).block();

        assertThat(result).isNull();
    }

    @Test
    void invalidateAccessToken_byString_error() {
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING))
            .willReturn(Mono.error(ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE, "expired")));

        ResponseEntity<Object> result = underTest.invalidateAccessToken(ACCESS_TOKEN_STRING).block();

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void invalidateAccessToken_byString_unexpectedError() {
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING))
            .willReturn(Mono.error(new RuntimeException("unexpected")));

        ResponseEntity<Object> result = underTest.invalidateAccessToken(ACCESS_TOKEN_STRING).block();

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void invalidateAccessToken_byString() {
        AccessToken accessToken = accessToken();
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING)).willReturn(Mono.just(accessToken));
        given(invalidatedAccessTokenService.contains(ACCESS_TOKEN_ID)).willReturn(false);
        given(invalidatedRefreshTokenService.contains(REFRESH_TOKEN_ID)).willReturn(false);

        ResponseEntity<Object> result = underTest.invalidateAccessToken(ACCESS_TOKEN_STRING).block();

        then(invalidatedAccessTokenService).should().add(ACCESS_TOKEN_ID);
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void checkSession_accessTokenInvalidated() {
        AccessToken accessToken = accessToken();
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING)).willReturn(Mono.just(accessToken));
        given(invalidatedAccessTokenService.contains(ACCESS_TOKEN_ID)).willReturn(true);

        ResponseEntity<Object> result = underTest.checkSession(ACCESS_TOKEN_STRING).block();

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void checkSession_refreshTokenInvalidated() {
        AccessToken accessToken = accessToken();
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING)).willReturn(Mono.just(accessToken));
        given(invalidatedAccessTokenService.contains(ACCESS_TOKEN_ID)).willReturn(false);
        given(invalidatedRefreshTokenService.contains(REFRESH_TOKEN_ID)).willReturn(true);

        ResponseEntity<Object> result = underTest.checkSession(ACCESS_TOKEN_STRING).block();

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void checkSession_error() {
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING))
            .willReturn(Mono.error(ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE, "expired")));

        ResponseEntity<Object> result = underTest.checkSession(ACCESS_TOKEN_STRING).block();

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void checkSession() {
        AccessToken accessToken = accessToken();
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING)).willReturn(Mono.just(accessToken));
        given(invalidatedAccessTokenService.contains(ACCESS_TOKEN_ID)).willReturn(false);
        given(invalidatedRefreshTokenService.contains(REFRESH_TOKEN_ID)).willReturn(false);

        ResponseEntity<Object> result = underTest.checkSession(ACCESS_TOKEN_STRING).block();

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getWebSocketProtocol() {
        OneParamResponse<String> result = underTest.getWebSocketProtocol().block();

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo("ws");
    }

    @Test
    void getOwnUserId_noSession() {
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING)).willReturn(Mono.empty());

        ResponseEntity<OneParamResponse<UUID>> result = underTest.getOwnUserId(ACCESS_TOKEN_STRING).block();

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getValue()).isNull();
    }

    @Test
    void getOwnUserId() {
        AccessToken accessToken = accessToken();
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING)).willReturn(Mono.just(accessToken));

        ResponseEntity<OneParamResponse<UUID>> result = underTest.getOwnUserId(ACCESS_TOKEN_STRING).block();

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getValue()).isEqualTo(USER_ID);
    }

    private AccessToken accessToken() {
        return AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID)
            .refreshTokenId(REFRESH_TOKEN_ID)
            .userId(USER_ID)
            .build();
    }
}