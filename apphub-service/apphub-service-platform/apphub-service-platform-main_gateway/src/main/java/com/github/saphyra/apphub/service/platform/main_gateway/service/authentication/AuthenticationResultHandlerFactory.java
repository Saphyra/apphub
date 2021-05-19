package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.util.UriUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class AuthenticationResultHandlerFactory {
    private final UriUtils uriUtils;
    private final ObjectMapperWrapper objectMapperWrapper;

    AuthenticationResultHandler unauthorized(HttpHeaders headers, ErrorResponseWrapper errorResponse) {
        if (uriUtils.isRestCall(headers)) {
            return new UnauthorizedRestHandler(errorResponse, objectMapperWrapper);
        } else {
            return new UnauthorizedWebHandler();
        }
    }

    public AuthenticationResultHandler authorized(String accessTokenHeader) {
        return new AuthorizedResultHandler(accessTokenHeader);
    }
}
