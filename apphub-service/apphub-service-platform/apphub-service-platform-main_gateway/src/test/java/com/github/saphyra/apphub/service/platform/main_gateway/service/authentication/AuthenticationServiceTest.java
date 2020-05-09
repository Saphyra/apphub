package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import com.github.saphyra.apphub.service.platform.main_gateway.util.ErrorResponseHandler;
import com.github.saphyra.util.CookieUtil;
import com.github.saphyra.util.ObjectMapperWrapper;
import com.netflix.zuul.context.RequestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_HEADER;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String ACCESS_TOKEN = "access-token";
    private static final String ENCODED_ACCESS_TOKEN = "encoded-access-token";
    private static final String LOCALE = "locale";

    @Mock
    private AccessTokenExpirationUpdateService accessTokenExpirationUpdateService;

    @Mock
    private AccessTokenHeaderFactory accessTokenHeaderFactory;

    @Mock
    private AccessTokenQueryService accessTokenQueryService;

    @Mock
    private Base64Encoder base64Encoder;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private ErrorResponseFactory errorResponseFactory;

    @Mock
    private ErrorResponseHandler errorResponseHandler;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private AuthenticationService underTest;

    @Mock
    private RequestContext requestContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private InternalAccessTokenResponse accessTokenResponse;

    @Mock
    private ErrorResponse errorResponse;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Before
    public void setUp() {
        given(requestContext.getRequest()).willReturn(request);
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.LOCALE_HEADER, LOCALE);
        given(requestContext.getZuulRequestHeaders()).willReturn(headers);
        given(errorResponseFactory.createErrorResponse(LOCALE, HttpStatus.UNAUTHORIZED, "NO_SESSION_AVAILABLE", new HashMap<>())).willReturn(errorResponse);
    }

    @Test
    public void cookieNotFound() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.empty());

        underTest.authenticate(requestContext);

        verify(errorResponseHandler).handleUnauthorized(requestContext, errorResponse);
    }

    @Test
    public void accessTokenNotFound() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.of(ACCESS_TOKEN_ID_STRING));
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.empty());

        underTest.authenticate(requestContext);

        verify(errorResponseHandler).handleUnauthorized(requestContext, errorResponse);
    }

    @Test
    public void authenticationSuccessful() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.of(ACCESS_TOKEN_ID_STRING));
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(accessTokenResponse));
        given(accessTokenHeaderFactory.create(accessTokenResponse)).willReturn(accessTokenHeader);
        given(objectMapperWrapper.writeValueAsString(accessTokenHeader)).willReturn(ACCESS_TOKEN);
        given(base64Encoder.encode(ACCESS_TOKEN)).willReturn(ENCODED_ACCESS_TOKEN);
        given(accessTokenResponse.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);

        underTest.authenticate(requestContext);

        verify(requestContext).addZuulRequestHeader(ACCESS_TOKEN_HEADER, ENCODED_ACCESS_TOKEN);
        verify(accessTokenExpirationUpdateService).updateExpiration(request, ACCESS_TOKEN_ID);
    }
}