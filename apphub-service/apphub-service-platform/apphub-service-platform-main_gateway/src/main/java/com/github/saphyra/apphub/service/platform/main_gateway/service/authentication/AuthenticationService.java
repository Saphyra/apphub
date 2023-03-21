package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization.AuthorizationService;
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
    private final AuthResultHandlerFactory authResultHandlerFactory;
    private final AuthorizationService authorizationService;

    public AuthResultHandler authenticate(ServerHttpRequest request) {
        Optional<String> accessTokenIdStringOptional = Optional.ofNullable(request.getCookies().getFirst(ACCESS_TOKEN_COOKIE))
            .map(HttpCookie::getValue);

        if (accessTokenIdStringOptional.isEmpty()) {
            log.debug("AccessToken cookie not present. Checking Auth header...");
            accessTokenIdStringOptional = Optional.ofNullable(request.getHeaders().getFirst(Constants.AUTHORIZATION_HEADER));
        }

        String locale = Optional.ofNullable(request.getHeaders().getFirst(Constants.LOCALE_HEADER))
            .orElseThrow(() -> new IllegalStateException("Locale header not found."));

        if (accessTokenIdStringOptional.isEmpty()) {
            log.debug("accessTokenCookie is not present. Sending error...");
            return authResultHandlerFactory.authenticationFailed(request.getHeaders(), createErrorResponse(locale));
        }

        Optional<InternalAccessTokenResponse> accessTokenResponseOptional = accessTokenQueryService.getAccessToken(accessTokenIdStringOptional.get());
        if (accessTokenResponseOptional.isEmpty()) {
            log.debug("AccessToken not found. Sending error...");
            return authResultHandlerFactory.authenticationFailed(request.getHeaders(), createErrorResponse(locale));
        }

        InternalAccessTokenResponse accessTokenResponse = accessTokenResponseOptional.get();
        AccessTokenHeader accessToken = accessTokenHeaderFactory.create(accessTokenResponse);

        Optional<AuthResultHandler> maybeUnauthorizedResultHandler = authorizationService.authorize(request, accessToken, locale);
        if (maybeUnauthorizedResultHandler.isPresent()) {
            return maybeUnauthorizedResultHandler.get();
        }

        String encodedAccessToken = accessTokenHeaderConverter.convertDomain(accessToken);
        log.debug("Enriching request with auth header: {}", encodedAccessToken);

        accessTokenExpirationUpdateService.updateExpiration(request.getMethod(), request.getURI().getPath(), accessTokenResponse.getAccessTokenId());

        return authResultHandlerFactory.authorized(encodedAccessToken);
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
