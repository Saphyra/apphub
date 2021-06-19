package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationResultHandlerFactoryTest {
    private static final String ACCESS_TOKEN_HEADER = "access-token-header";

    @Mock
    private UriUtils uriUtils;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private AuthenticationResultHandlerFactory underTest;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private ErrorResponseWrapper errorResponseWrapper;

    @Test
    public void unauthorized_restCall() {
        given(uriUtils.isRestCall(httpHeaders)).willReturn(true);

        AuthenticationResultHandler result = underTest.unauthorized(httpHeaders, errorResponseWrapper);

        assertThat(result).isInstanceOf(UnauthorizedRestHandler.class);
        UnauthorizedRestHandler restHandler = (UnauthorizedRestHandler) result;
        assertThat(restHandler.getErrorResponse()).isEqualTo(errorResponseWrapper);
        assertThat(restHandler.getObjectMapperWrapper()).isEqualTo(objectMapperWrapper);
    }

    @Test
    public void unauthorized_webCall() {
        given(uriUtils.isRestCall(httpHeaders)).willReturn(false);

        AuthenticationResultHandler result = underTest.unauthorized(httpHeaders, errorResponseWrapper);

        assertThat(result).isInstanceOf(UnauthorizedWebHandler.class);
    }

    @Test
    public void authorized() {
        AuthenticationResultHandler result = underTest.authorized(ACCESS_TOKEN_HEADER);

        assertThat(result).isInstanceOf(AuthorizedResultHandler.class);
        AuthorizedResultHandler resultHandler = (AuthorizedResultHandler) result;
        assertThat(resultHandler.getAccessTokenHeader()).isEqualTo(ACCESS_TOKEN_HEADER);
    }
}