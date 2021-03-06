package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.CookieUtil;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import com.github.saphyra.apphub.service.platform.main_gateway.util.ErrorResponseHandler;
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
            log.info("AccessToken cookie not present. Checking Auth header...");
            accessTokenIdStringOptional = Optional.ofNullable(requestContext.getRequest().getHeader(Constants.AUTHORIZATION_HEADER));
        }

        if (!accessTokenIdStringOptional.isPresent()) {
            log.info("accessTokenCookie is not present. Sending error...");
            errorResponseHandler.sendErrorResponse(requestContext, createErrorResponse(requestContext));
            return;
        }

        Optional<InternalAccessTokenResponse> accessTokenResponseOptional = accessTokenQueryService.getAccessToken(accessTokenIdStringOptional.get());
        if (!accessTokenResponseOptional.isPresent()) {
            log.info("AccessToken not found. Sending error...");
            errorResponseHandler.sendErrorResponse(requestContext, createErrorResponse(requestContext));
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

    private ErrorResponseWrapper createErrorResponse(RequestContext requestContext) {
        String locale = requestContext.getZuulRequestHeaders()
            .get(Constants.LOCALE_HEADER);
        return errorResponseFactory.create(
            locale,
            HttpStatus.UNAUTHORIZED,
            ErrorCode.NO_SESSION_AVAILABLE.name(),
            new HashMap<>()
        );
    }
}
