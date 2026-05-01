package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication.AuthenticationFailedWebAuthResultHandler;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthResultHandlerFactory {
    private final UriUtils uriUtils;
    private final ObjectMapper objectMapper;
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;

    AuthResultHandler unauthorized(HttpHeaders headers, ErrorResponseWrapper errorResponse) {
        if (uriUtils.isRestCall(headers)) {
            return new ErrorRestHandler(errorResponse, objectMapper);
        } else {
            return new AuthorizationFailedAuthResultHandler();
        }
    }

    public AuthResultHandler authorized(AccessToken accessToken) {
        return new AuthorizedResultHandler(accessTokenHeaderConverter.convertDomain(accessToken));
    }

    public AuthResultHandler authenticationFailed(HttpHeaders headers, ErrorResponseWrapper errorResponse, String redirectUrl) {
        if (uriUtils.isRestCall(headers)) {
            return new ErrorRestHandler(errorResponse, objectMapper);
        } else {
            return new AuthenticationFailedWebAuthResultHandler(redirectUrl);
        }
    }
}
