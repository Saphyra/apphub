package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.service.ErrorResponseFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.AuthResultHandler;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.AuthResultHandlerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final MatchingRoleProvider matchingRoleProvider;
    private final RequiredRoleChecker requiredRoleChecker;
    private final AuthResultHandlerFactory authResultHandlerFactory;
    private final ErrorResponseFactory errorResponseFactory;
    private final RedirectUrlProvider redirectUrlProvider;

    /**
     * Checks if the AccessToken has the required roles to call the URL of the request.
     * @return empty Mono if authentication was successful, Mono<AuthResultHandler> if authentication failed.
     */
    public Mono<AuthResultHandler> authenticate(ServerHttpRequest request, AccessToken accessToken) {
        //Find RoleSettings matching the request
        return Mono.just(matchingRoleProvider.getMatchingSettings(request))
            //Collect required roles
            .map(roleSettings -> roleSettings.stream().flatMap(roleSetting -> roleSetting.getRequiredRoles().stream()).collect(Collectors.toSet()))
            //Check if user has all the required roles
            .filter(requiredRoles -> !requiredRoles.isEmpty() && !requiredRoleChecker.hasRequiredRoles(requiredRoles, accessToken))
            //Return error if required role is not granted
            .map(roleSettings -> authResultHandlerFactory.authenticationFailed(request.getHeaders(), createErrorResponse(), redirectUrlProvider.getRedirectUrl(roleSettings, accessToken)));
    }

    private ErrorResponseWrapper createErrorResponse() {
        return errorResponseFactory.create(
            HttpStatus.FORBIDDEN,
            ErrorCode.MISSING_ROLE
        );
    }
}
