package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.RequestHelper;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseWrapper;
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

@RequiredArgsConstructor
@Component
@Slf4j
class RoleFilter extends OncePerRequestFilter {
    private final ErrorResponseFactory errorResponseFactory;
    private final MatchingRoleProvider matchingRoleProvider;
    private final RequestHelper requestHelper;
    private final RequiredRoleChecker requiredRoleChecker;

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
                response.sendRedirect(String.format("%s?error_code=%s", Endpoints.ERROR_PAGE, ErrorCode.MISSING_ROLE.name()));
            }
        }
    }

    private void sendRestError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorResponseWrapper errorResponse = errorResponseFactory.create(
            request,
            HttpStatus.FORBIDDEN,
            ErrorCode.MISSING_ROLE.name()
        );

        requestHelper.sendRestError(
            response,
            errorResponse.getStatus(),
            errorResponse.getErrorResponse()
        );
    }
}
