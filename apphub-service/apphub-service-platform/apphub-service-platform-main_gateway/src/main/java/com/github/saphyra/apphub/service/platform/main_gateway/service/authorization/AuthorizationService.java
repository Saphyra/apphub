package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.service.ErrorResponseFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;

import static com.github.saphyra.apphub.lib.common_domain.Constants.ACCESS_TOKEN_COOKIE;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AuthorizationService {
    private final ErrorResponseFactory errorResponseFactory;
    private final AuthResultHandlerFactory authResultHandlerFactory;
    private final AuthenticationService authenticationService;
    private final TokenParser tokenParser;
    private final ObjectMapper objectMapper;

    public Mono<AuthResultHandler> authorize(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getCookies().getFirst(ACCESS_TOKEN_COOKIE)) //Get AccessToken from cookie (Web call)
            .map(HttpCookie::getValue)
            .switchIfEmpty(Mono.justOrEmpty(request.getHeaders().getFirst(Constants.AUTHORIZATION_HEADER)))//Get AccessToken from Authorization header (Mobile call)
            .flatMap(tokenParser::verifyAccessToken) //Parse and verify JWT
            .flatMap(
                accessToken -> authenticationService.authenticate(request, accessToken) //Check if necessary roles granted
                    .switchIfEmpty(Mono.fromSupplier(() -> authResultHandlerFactory.authorized(accessToken)))
            ) //If authenticationService returned empty, then return success
            .switchIfEmpty(Mono.fromSupplier(() -> {
                log.warn("No accessToken found.");

                return authResultHandlerFactory.unauthorized(request.getHeaders(), createErrorResponse());
            }))//Return unauthorized if no accessToken sent
            //TODO handle (report/log) error
            .onErrorResume(throwable -> {
                log.error("Error during authorization", throwable);

                return Mono.just(authResultHandlerFactory.unauthorized(request.getHeaders(), createErrorResponse()));
            }); //Handle errors (e.g. token vas invalid)
    }

    private ErrorResponseWrapper createErrorResponse() {
        return errorResponseFactory.create(
            HttpStatus.UNAUTHORIZED,
            ErrorCode.NO_SESSION_AVAILABLE,
            new HashMap<>()
        );
    }
}
