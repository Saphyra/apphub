package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.translation.ErrorResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

import static com.github.saphyra.apphub.lib.common_domain.Constants.ACCESS_TOKEN_COOKIE;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AccessTokenExpirationUpdateService accessTokenExpirationUpdateService;
    private final AccessTokenHeaderFactory accessTokenHeaderFactory;
    private final AccessTokenQueryService accessTokenQueryService;
    private final ErrorResponseFactory errorResponseFactory;
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final AuthenticationResultHandlerFactory authenticationResultHandlerFactory;

    public AuthenticationResultHandler authenticate(ServerHttpRequest request) {
        Optional<String> accessTokenIdStringOptional = Optional.ofNullable(request.getCookies().getFirst(ACCESS_TOKEN_COOKIE))
            .map(HttpCookie::getValue);

        if (!accessTokenIdStringOptional.isPresent()) {
            log.debug("AccessToken cookie not present. Checking Auth header...");
            accessTokenIdStringOptional = Optional.ofNullable(request.getHeaders().getFirst(Constants.AUTHORIZATION_HEADER));
        }

        String locale = Optional.ofNullable(request.getHeaders().getFirst(Constants.LOCALE_HEADER))
            .orElseThrow(() -> new IllegalStateException("Locale header not found."));

        if (!accessTokenIdStringOptional.isPresent()) {
            log.debug("accessTokenCookie is not present. Sending error...");
            return authenticationResultHandlerFactory.unauthorized(request.getHeaders(), createErrorResponse(locale));
        }

        Optional<InternalAccessTokenResponse> accessTokenResponseOptional = accessTokenQueryService.getAccessToken(accessTokenIdStringOptional.get());
        if (!accessTokenResponseOptional.isPresent()) {
            log.debug("AccessToken not found. Sending error...");
            return authenticationResultHandlerFactory.unauthorized(request.getHeaders(), createErrorResponse(locale));
        }

        InternalAccessTokenResponse accessTokenResponse = accessTokenResponseOptional.get();
        AccessTokenHeader accessToken = accessTokenHeaderFactory.create(accessTokenResponse);
        String encodedAccessToken = accessTokenHeaderConverter.convertDomain(accessToken);
        log.debug("Enriching request with auth header: {}", encodedAccessToken);

        accessTokenExpirationUpdateService.updateExpiration(request.getMethodValue(), request.getURI().getPath(), accessTokenResponse.getAccessTokenId());

        return authenticationResultHandlerFactory.authorized(encodedAccessToken);
    }

    private ErrorResponseWrapper createErrorResponse(String locale) {
        return errorResponseFactory.create(
            locale,
            HttpStatus.UNAUTHORIZED,
            ErrorCode.NO_SESSION_AVAILABLE,
            new HashMap<>()
        );
    }
}
