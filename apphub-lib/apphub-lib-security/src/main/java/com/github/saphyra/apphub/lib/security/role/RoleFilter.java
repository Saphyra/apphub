package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.error_handler.service.translation.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
class RoleFilter extends OncePerRequestFilter {
    private final ErrorResponseFactory errorResponseFactory;
    private final MatchingRoleProvider matchingRoleProvider;
    private final RequestHelper requestHelper;
    private final RequiredRoleChecker requiredRoleChecker;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        List<RoleSetting> roleSettings = matchingRoleProvider.getMatchingSettings(request);
        log.info("Role settings: {}", roleSettings);

        if (roleSettings.isEmpty() || requiredRoleChecker.hasRequiredRoles(roleSettings)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("User does not have the required role to call endpoint {} - {}", request.getMethod(), request.getRequestURI());
            if (requestHelper.isRestCall(request)) {
                sendRestError(request, response);
            } else {
                String redirectUrl = getRedirectUrl(roleSettings);
                response.sendRedirect(redirectUrl);
            }
        }
    }

    private String getRedirectUrl(List<RoleSetting> matchingSettings) {
        String redirectUrl = String.format("%s?error_code=%s", Endpoints.ERROR_PAGE, ErrorCode.MISSING_ROLE.name());
        Optional<AccessTokenHeader> accessTokenHeader = accessTokenProvider.getOptional();
        if (accessTokenHeader.isPresent()) {
            redirectUrl += "&user_id=" + accessTokenHeader.get()
                .getUserId();
            redirectUrl += "&required_roles=" + matchingSettings.stream()
                .flatMap(roleSetting -> roleSetting.getRequiredRoles().stream())
                .distinct()
                .collect(Collectors.joining(","));
        }
        return redirectUrl;
    }

    private void sendRestError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorResponseWrapper errorResponse = errorResponseFactory.create(
            request,
            HttpStatus.FORBIDDEN,
            ErrorCode.MISSING_ROLE
        );

        requestHelper.sendRestError(
            response,
            errorResponse.getStatus(),
            errorResponse.getErrorResponse()
        );
    }
}
