package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.service.ErrorResponseFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedAccessTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedRefreshTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication.AuthenticationService;
import com.github.saphyra.apphub.service.platform.main_gateway.util.ErrorLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {
    private static final String JWT = "jwt";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();

    @Mock
    private ErrorResponseFactory errorResponseFactory;

    @Mock
    private AuthResultHandlerFactory authResultHandlerFactory;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TokenParser tokenParser;

    @Mock
    private InvalidatedAccessTokenService invalidatedAccessTokenService;

    @Mock
    private InvalidatedRefreshTokenService invalidatedRefreshTokenService;

    @Mock
    private ErrorLogger errorLogger;

    @InjectMocks
    private AuthorizationService underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private ErrorResponseWrapper errorResponseWrapper;

    @Mock
    private AuthResultHandler authResultHandler;

    @Mock
    private AccessToken accessToken;

    @Test
    void accessTokenNotPresent() {
        given(request.getCookies()).willReturn(new LinkedMultiValueMap<>());
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(HttpHeaders.AUTHORIZATION)).willReturn(null);
        given(errorResponseFactory.create(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE)).willReturn(errorResponseWrapper);
        given(authResultHandlerFactory.unauthorized(httpHeaders, errorResponseWrapper)).willReturn(authResultHandler);

        StepVerifier.create(underTest.authorize(request))
            .expectNext(authResultHandler)
            .verifyComplete();
    }

    @Test
    void tokenVerificationFailed() {
        given(request.getCookies()).willReturn(new LinkedMultiValueMap<>());
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(HttpHeaders.AUTHORIZATION)).willReturn(JWT);
        Throwable ex = new RuntimeException("asd");
        given(tokenParser.verifyAccessToken(JWT)).willReturn(Mono.error(() -> ex));
        given(errorResponseFactory.create(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE)).willReturn(errorResponseWrapper);
        given(authResultHandlerFactory.unauthorized(httpHeaders, errorResponseWrapper)).willReturn(authResultHandler);

        StepVerifier.create(underTest.authorize(request))
            .expectNext(authResultHandler)
            .verifyComplete();
    }

    @Test
    void accessTokenInvalidated() {
        given(request.getCookies()).willReturn(new LinkedMultiValueMap<>());
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(HttpHeaders.AUTHORIZATION)).willReturn(JWT);
        given(tokenParser.verifyAccessToken(JWT)).willReturn(Mono.just(accessToken));
        given(accessToken.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(invalidatedAccessTokenService.contains(ACCESS_TOKEN_ID)).willReturn(true);
        given(errorResponseFactory.create(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE)).willReturn(errorResponseWrapper);
        given(authResultHandlerFactory.unauthorized(httpHeaders, errorResponseWrapper)).willReturn(authResultHandler);

        StepVerifier.create(underTest.authorize(request))
            .expectNext(authResultHandler)
            .verifyComplete();
    }

    @Test
    void refreshTokenInvalidated() {
        given(request.getCookies()).willReturn(new LinkedMultiValueMap<>());
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(HttpHeaders.AUTHORIZATION)).willReturn(JWT);
        given(tokenParser.verifyAccessToken(JWT)).willReturn(Mono.just(accessToken));
        given(accessToken.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(invalidatedAccessTokenService.contains(ACCESS_TOKEN_ID)).willReturn(false);
        given(accessToken.getRefreshTokenId()).willReturn(REFRESH_TOKEN_ID);
        given(invalidatedRefreshTokenService.contains(REFRESH_TOKEN_ID)).willReturn(true);
        given(errorResponseFactory.create(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE)).willReturn(errorResponseWrapper);
        given(authResultHandlerFactory.unauthorized(httpHeaders, errorResponseWrapper)).willReturn(authResultHandler);

        StepVerifier.create(underTest.authorize(request))
            .expectNext(authResultHandler)
            .verifyComplete();
    }

    @Test
    void authenticationFailed() {
        given(request.getCookies()).willReturn(new LinkedMultiValueMap<>());
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(HttpHeaders.AUTHORIZATION)).willReturn(JWT);
        given(tokenParser.verifyAccessToken(JWT)).willReturn(Mono.just(accessToken));
        given(accessToken.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(invalidatedAccessTokenService.contains(ACCESS_TOKEN_ID)).willReturn(false);
        given(accessToken.getRefreshTokenId()).willReturn(REFRESH_TOKEN_ID);
        given(invalidatedRefreshTokenService.contains(REFRESH_TOKEN_ID)).willReturn(false);
        given(authenticationService.authenticate(request, accessToken)).willReturn(Mono.just(authResultHandler));

        StepVerifier.create(underTest.authorize(request))
            .expectNext(authResultHandler)
            .verifyComplete();
    }

    @Test
    void authorized() {
        given(request.getCookies()).willReturn(new LinkedMultiValueMap<>());
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(HttpHeaders.AUTHORIZATION)).willReturn(JWT);
        given(tokenParser.verifyAccessToken(JWT)).willReturn(Mono.just(accessToken));
        given(accessToken.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(invalidatedAccessTokenService.contains(ACCESS_TOKEN_ID)).willReturn(false);
        given(accessToken.getRefreshTokenId()).willReturn(REFRESH_TOKEN_ID);
        given(invalidatedRefreshTokenService.contains(REFRESH_TOKEN_ID)).willReturn(false);
        given(authenticationService.authenticate(request, accessToken)).willReturn(Mono.empty());
        given(authResultHandlerFactory.authorized(accessToken)).willReturn(authResultHandler);

        StepVerifier.create(underTest.authorize(request))
            .expectNext(authResultHandler)
            .verifyComplete();
    }
}