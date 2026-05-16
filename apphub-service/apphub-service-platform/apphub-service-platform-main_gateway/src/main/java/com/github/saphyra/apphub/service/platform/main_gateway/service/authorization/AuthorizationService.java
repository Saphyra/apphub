package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.service.ErrorResponseFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedAccessTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedRefreshTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication.AuthenticationService;
import com.github.saphyra.apphub.service.platform.main_gateway.util.ErrorLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.github.saphyra.apphub.lib.common_domain.Constants.ACCESS_TOKEN_COOKIE;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {
    private final ErrorResponseFactory errorResponseFactory;
    private final AuthResultHandlerFactory authResultHandlerFactory;
    private final AuthenticationService authenticationService;
    private final TokenParser tokenParser;
    private final InvalidatedAccessTokenService  invalidatedAccessTokenService;
    private final InvalidatedRefreshTokenService invalidatedRefreshTokenService;
    private final ErrorLogger errorLogger;

    public Mono<AuthResultHandler> authorize(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getCookies().getFirst(ACCESS_TOKEN_COOKIE)) //Get AccessToken from cookie (Web call)
            .map(HttpCookie::getValue)
            .switchIfEmpty(Mono.justOrEmpty(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)))//Get AccessToken from Authorization header (Mobile call)
            .flatMap(tokenParser::verifyAccessToken) //Parse and verify JWT
            .filter(accessToken -> !invalidatedAccessTokenService.contains(accessToken.getAccessTokenId()))
            .filter(accessToken -> !invalidatedRefreshTokenService.contains(accessToken.getRefreshTokenId()))
            .flatMap(
                accessToken -> authenticationService.authenticate(request, accessToken) //Check if necessary roles granted
                    .switchIfEmpty(Mono.fromSupplier(() -> authResultHandlerFactory.authorized(accessToken)))
            ) //If authenticationService returned empty, then return success
            .switchIfEmpty(Mono.fromSupplier(() -> authResultHandlerFactory.unauthorized(request.getHeaders(), createErrorResponse())))//Return unauthorized if no accessToken sent
            .onErrorResume(throwable -> {
                errorLogger.log(throwable);

                return Mono.just(authResultHandlerFactory.unauthorized(request.getHeaders(), createErrorResponse()));
            }); //Handle errors (e.g. token vas invalid)
    }

    private ErrorResponseWrapper createErrorResponse() {
        return errorResponseFactory.create(
            HttpStatus.UNAUTHORIZED,
            ErrorCode.NO_SESSION_AVAILABLE
        );
    }
}
