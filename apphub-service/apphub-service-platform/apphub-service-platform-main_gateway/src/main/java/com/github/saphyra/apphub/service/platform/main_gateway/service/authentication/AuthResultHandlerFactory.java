package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization.AuthorizationFailedWebHandler;
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

    AuthResultHandler authenticationFailed(HttpHeaders headers, ErrorResponseWrapper errorResponse) {
        if (uriUtils.isRestCall(headers)) {
            return new ErrorRestHandler(errorResponse, objectMapper);
        } else {
            return new AuthenticationFailedWebHandler();
        }
    }

    public AuthResultHandler authorized(String accessTokenHeader) {
        return new AuthorizedResultHandler(accessTokenHeader);
    }

    public AuthResultHandler unauthorized(HttpHeaders headers, ErrorResponseWrapper errorResponse, String redirectUrl) {
        if (uriUtils.isRestCall(headers)) {
            return new ErrorRestHandler(errorResponse, objectMapper);
        } else {
            return new AuthorizationFailedWebHandler(redirectUrl);
        }
    }
}
