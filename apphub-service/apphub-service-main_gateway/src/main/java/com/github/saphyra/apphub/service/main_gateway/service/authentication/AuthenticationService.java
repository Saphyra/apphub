package com.github.saphyra.apphub.service.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.service.main_gateway.util.ErrorResponseHandler;
import com.github.saphyra.util.CookieUtil;
import com.github.saphyra.util.ObjectMapperWrapper;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_COOKIE;
import static com.github.saphyra.apphub.lib.common_util.Constants.ACCESS_TOKEN_HEADER;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AccessTokenIdConverter accessTokenIdConverter;
    private final AccessTokenQueryService accessTokenQueryService;
    private final Base64Encoder base64Encoder;
    private final CookieUtil cookieUtil;
    private final ErrorResponseHandler errorResponseHandler;
    private final ObjectMapperWrapper objectMapperWrapper;

    public void authenticate(RequestContext requestContext) {
        Optional<String> accessTokenIdStringOptional = cookieUtil.getCookie(
            requestContext.getRequest(),
            ACCESS_TOKEN_COOKIE
        );

        if (!accessTokenIdStringOptional.isPresent()) {
            errorResponseHandler.handleUnauthorized(requestContext, ""); //TODO add proper response
            return;
        }

        String accessTokenIdString = accessTokenIdStringOptional.get();
        Optional<UUID> accessTokenIdOptional = accessTokenIdConverter.convertAccessTokenId(accessTokenIdString);
        if (!accessTokenIdOptional.isPresent()) {
            errorResponseHandler.handleBadRequest(requestContext, ""); //TODO add proper response
            return;
        }

        Optional<InternalAccessTokenResponse> accessTokenResponseOptional = accessTokenQueryService.getAccessToken(accessTokenIdOptional.get());
        if (!accessTokenResponseOptional.isPresent()) {
            errorResponseHandler.handleUnauthorized(requestContext, ""); //TODO add proper response
            return;
        }

        String accessTokenString = objectMapperWrapper.writeValueAsString(accessTokenResponseOptional.get());
        log.debug("Stringified accessToken: {}", accessTokenString);
        String encodedAccessToken = base64Encoder.encode(accessTokenString);
        log.debug("Enriching request with auth header: {}", encodedAccessToken);
        requestContext.addZuulRequestHeader(ACCESS_TOKEN_HEADER, encodedAccessToken);
    }
}
