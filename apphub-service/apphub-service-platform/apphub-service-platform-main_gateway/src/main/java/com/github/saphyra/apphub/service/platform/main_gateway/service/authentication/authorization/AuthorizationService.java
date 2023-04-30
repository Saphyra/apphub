package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.AuthResultHandler;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.AuthResultHandlerFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.service.translation.ErrorResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {
    private final MatchingRoleProvider matchingRoleProvider;
    private final RequiredRoleChecker requiredRoleChecker;
    private final AuthResultHandlerFactory authResultHandlerFactory;
    private final ErrorResponseFactory errorResponseFactory;
    private final RedirectUrlProvider redirectUrlProvider;

    public Optional<AuthResultHandler> authorize(ServerHttpRequest request, AccessTokenHeader accessToken, String locale) {
        List<RoleSetting> roleSettings = matchingRoleProvider.getMatchingSettings(request);

        if (roleSettings.isEmpty() || requiredRoleChecker.hasRequiredRoles(roleSettings, accessToken)) {
            return Optional.empty();
        }

        return Optional.of(authResultHandlerFactory.unauthorized(request.getHeaders(), createErrorResponse(locale), redirectUrlProvider.getRedirectUrl(roleSettings, accessToken)));
    }

    private ErrorResponseWrapper createErrorResponse(String locale) {
        return errorResponseFactory.create(
            locale,
            HttpStatus.FORBIDDEN,
            ErrorCode.MISSING_ROLE,
            new HashMap<>()
        );
    }
}
