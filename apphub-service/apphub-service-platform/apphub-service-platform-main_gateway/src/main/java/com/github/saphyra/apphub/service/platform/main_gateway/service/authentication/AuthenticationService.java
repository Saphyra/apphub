package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.aphub.lib.model.ErrorResponse;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import com.github.saphyra.apphub.service.platform.main_gateway.util.ErrorResponseHandler;
import com.github.saphyra.util.CookieUtil;
import com.github.saphyra.util.ObjectMapperWrapper;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_COOKIE;
import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_HEADER;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private static final String NO_SESSION_AVAILABLE_ERROR_CODE = "NO_SESSION_AVAILABLE";

    private final AccessTokenExpirationUpdateService accessTokenExpirationUpdateService;
    private final AccessTokenHeaderFactory accessTokenHeaderFactory;
    private final AccessTokenQueryService accessTokenQueryService;
    private final Base64Encoder base64Encoder;
    private final CookieUtil cookieUtil;
    private final ErrorResponseFactory errorResponseFactory;
    private final ErrorResponseHandler errorResponseHandler;
    private final ObjectMapperWrapper objectMapperWrapper;

    public void authenticate(RequestContext requestContext) {
        Optional<String> accessTokenIdStringOptional = cookieUtil.getCookie(
            requestContext.getRequest(),
            ACCESS_TOKEN_COOKIE
        );

        if (!accessTokenIdStringOptional.isPresent()) {
            errorResponseHandler.handleUnauthorized(requestContext, createErrorResponse(requestContext));
            return;
        }

        Optional<InternalAccessTokenResponse> accessTokenResponseOptional = accessTokenQueryService.getAccessToken(accessTokenIdStringOptional.get());
        if (!accessTokenResponseOptional.isPresent()) {
            errorResponseHandler.handleUnauthorized(requestContext, createErrorResponse(requestContext));
            return;
        }

        InternalAccessTokenResponse accessTokenResponse = accessTokenResponseOptional.get();
        AccessTokenHeader accessToken = accessTokenHeaderFactory.create(accessTokenResponse);
        String accessTokenString = objectMapperWrapper.writeValueAsString(accessToken);
        log.debug("Stringified accessToken: {}", accessTokenString);
        String encodedAccessToken = base64Encoder.encode(accessTokenString);
        log.debug("Enriching request with auth header: {}", encodedAccessToken);

        requestContext.addZuulRequestHeader(ACCESS_TOKEN_HEADER, encodedAccessToken);

        accessTokenExpirationUpdateService.updateExpiration(requestContext.getRequest(), accessTokenResponse.getAccessTokenId());
    }

    private ErrorResponse createErrorResponse(RequestContext requestContext) {
        String locale = requestContext.getZuulRequestHeaders()
            .get(Constants.LOCALE_HEADER);
        return errorResponseFactory.createErrorResponse(
            locale,
            HttpStatus.UNAUTHORIZED,
            ErrorCode.NO_SESSION_AVAILABLE.name(),
            new HashMap<>()
        );
    }
}
