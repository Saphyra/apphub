package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.user.model.login.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization.AuthorizationService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.translation.ErrorResponseFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    private static final String LOCALE = "locale";
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final String ENCODED_ACCESS_TOKEN = "encoded-access-token";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String PATH = "/path";

    @Mock
    private AccessTokenExpirationUpdateService accessTokenExpirationUpdateService;

    @Mock
    private AccessTokenHeaderFactory accessTokenHeaderFactory;

    @Mock
    private AccessTokenQueryService accessTokenQueryService;

    @Mock
    private ErrorResponseFactory errorResponseFactory;

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Mock
    private AuthResultHandlerFactory authResultHandlerFactory;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private AuthenticationService underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private HttpHeaders httpHeaders;

    private final MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();

    @Mock
    private ErrorResponseWrapper errorResponseWrapper;

    @Mock
    private AuthResultHandler resultHandler;

    @Mock
    private HttpCookie cookie;

    @Mock
    private InternalAccessTokenResponse internalAccessTokenResponse;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void noAccessTokenId() {
        given(request.getCookies()).willReturn(cookies);
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(Constants.AUTHORIZATION_HEADER)).willReturn(null);
        given(httpHeaders.getFirst(Constants.LOCALE_HEADER)).willReturn(LOCALE);
        given(errorResponseFactory.create(LOCALE, HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE, new HashMap<>())).willReturn(errorResponseWrapper);
        given(authResultHandlerFactory.authenticationFailed(httpHeaders, errorResponseWrapper)).willReturn(resultHandler);

        AuthResultHandler result = underTest.authenticate(request);

        assertThat(result).isEqualTo(resultHandler);
    }

    @Test
    public void accessTokenNotFound() {
        given(request.getCookies()).willReturn(cookies);
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(Constants.LOCALE_HEADER)).willReturn(LOCALE);
        given(errorResponseFactory.create(LOCALE, HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE, new HashMap<>())).willReturn(errorResponseWrapper);
        cookies.put(Constants.ACCESS_TOKEN_COOKIE, Arrays.asList(cookie));
        given(cookie.getValue()).willReturn(ACCESS_TOKEN_ID_STRING);
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.empty());
        given(authResultHandlerFactory.authenticationFailed(httpHeaders, errorResponseWrapper)).willReturn(resultHandler);

        AuthResultHandler result = underTest.authenticate(request);

        assertThat(result).isEqualTo(resultHandler);
    }

    @Test
    public void unauthorized() {
        given(request.getCookies()).willReturn(cookies);
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(Constants.LOCALE_HEADER)).willReturn(LOCALE);
        cookies.put(Constants.ACCESS_TOKEN_COOKIE, Arrays.asList(cookie));
        given(cookie.getValue()).willReturn(ACCESS_TOKEN_ID_STRING);
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(internalAccessTokenResponse));
        given(accessTokenHeaderFactory.create(internalAccessTokenResponse)).willReturn(accessTokenHeader);

        given(authorizationService.authorize(request, accessTokenHeader, LOCALE)).willReturn(Optional.of(resultHandler));

        AuthResultHandler result = underTest.authenticate(request);

        assertThat(result).isEqualTo(resultHandler);

        verify(authResultHandlerFactory, times(0)).authorized(any());
    }

    @Test
    public void authenticated() throws URISyntaxException {
        given(request.getCookies()).willReturn(cookies);
        given(request.getHeaders()).willReturn(httpHeaders);
        given(httpHeaders.getFirst(Constants.LOCALE_HEADER)).willReturn(LOCALE);
        cookies.put(Constants.ACCESS_TOKEN_COOKIE, Arrays.asList(cookie));
        given(cookie.getValue()).willReturn(ACCESS_TOKEN_ID_STRING);
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(internalAccessTokenResponse));
        given(accessTokenHeaderFactory.create(internalAccessTokenResponse)).willReturn(accessTokenHeader);
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ENCODED_ACCESS_TOKEN);
        given(authResultHandlerFactory.authorized(ENCODED_ACCESS_TOKEN)).willReturn(resultHandler);
        given(internalAccessTokenResponse.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(request.getMethod()).willReturn(HttpMethod.POST);
        given(request.getURI()).willReturn(new URI(UrlFactory.create(1000, PATH)));
        given(authorizationService.authorize(request, accessTokenHeader, LOCALE)).willReturn(Optional.empty());

        AuthResultHandler result = underTest.authenticate(request);

        assertThat(result).isEqualTo(resultHandler);

        verify(authorizationService).authorize(request, accessTokenHeader, LOCALE);
        verify(accessTokenExpirationUpdateService).updateExpiration(HttpMethod.POST, PATH, ACCESS_TOKEN_ID);
    }
}