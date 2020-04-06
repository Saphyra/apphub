package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.Constants;
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

import javax.servlet.http.HttpServletRequest;
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

    @Mock
    private AccessTokenIdConverter accessTokenIdConverter;

    @Mock
    private AccessTokenQueryService accessTokenQueryService;

    @Mock
    private Base64Encoder base64Encoder;

    @Mock
    private CookieUtil cookieUtil;

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

    @Before
    public void setUp() {
        given(requestContext.getRequest()).willReturn(request);
    }

    @Test
    public void cookieNotFound() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.empty());

        underTest.authenticate(requestContext);

        verify(errorResponseHandler).handleUnauthorized(requestContext, "");
    }

    @Test
    public void invalidAccessToken() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.of(ACCESS_TOKEN_ID_STRING));
        given(accessTokenIdConverter.convertAccessTokenId(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.empty());

        underTest.authenticate(requestContext);

        verify(errorResponseHandler).handleBadRequest(requestContext, "");
    }

    @Test
    public void accessTokenNotFound() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.of(ACCESS_TOKEN_ID_STRING));
        given(accessTokenIdConverter.convertAccessTokenId(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(ACCESS_TOKEN_ID));
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID)).willReturn(Optional.empty());

        underTest.authenticate(requestContext);

        verify(errorResponseHandler).handleUnauthorized(requestContext, "");
    }

    @Test
    public void authenticationSuccessful() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.of(ACCESS_TOKEN_ID_STRING));
        given(accessTokenIdConverter.convertAccessTokenId(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(ACCESS_TOKEN_ID));
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID)).willReturn(Optional.of(accessTokenResponse));
        given(objectMapperWrapper.writeValueAsString(accessTokenResponse)).willReturn(ACCESS_TOKEN);
        given(base64Encoder.encode(ACCESS_TOKEN)).willReturn(ENCODED_ACCESS_TOKEN);

        underTest.authenticate(requestContext);

        verify(requestContext).addZuulRequestHeader(ACCESS_TOKEN_HEADER, ENCODED_ACCESS_TOKEN);
    }
}