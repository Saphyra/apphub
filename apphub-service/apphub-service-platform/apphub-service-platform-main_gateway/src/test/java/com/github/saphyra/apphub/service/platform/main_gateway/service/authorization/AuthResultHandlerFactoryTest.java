package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthResultHandlerFactoryTest {
    private static final String ACCESS_TOKEN_HEADER = "access-token-header";

    @Mock
    private UriUtils uriUtils;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @InjectMocks
    private AuthResultHandlerFactory underTest;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private ErrorResponseWrapper errorResponseWrapper;

    @Mock
    private AccessToken accessToken;

    @Test
    public void authenticationFailed_restCall() {
        given(uriUtils.isRestCall(httpHeaders)).willReturn(true);

        AuthResultHandler result = underTest.unauthorized(httpHeaders, errorResponseWrapper);

        assertThat(result).isInstanceOf(ErrorRestHandler.class);
        ErrorRestHandler restHandler = (ErrorRestHandler) result;
        assertThat(restHandler.getErrorResponse()).isEqualTo(errorResponseWrapper);
        assertThat(restHandler.getObjectMapper()).isEqualTo(objectMapper);
    }

    @Test
    public void authenticationFailed_webCall() {
        given(uriUtils.isRestCall(httpHeaders)).willReturn(false);

        AuthResultHandler result = underTest.unauthorized(httpHeaders, errorResponseWrapper);

        assertThat(result).isInstanceOf(AuthorizationFailedAuthResultHandler.class);
    }

    @Test
    public void authorized() {
        given(accessTokenHeaderConverter.convertDomain(accessToken)).willReturn(ACCESS_TOKEN_HEADER);

        AuthResultHandler result = underTest.authorized(accessToken);

        assertThat(result).isInstanceOf(AuthorizedResultHandler.class);
        AuthorizedResultHandler resultHandler = (AuthorizedResultHandler) result;
        assertThat(resultHandler.getAccessTokenHeader()).isEqualTo(ACCESS_TOKEN_HEADER);
    }
}