package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.RequestHelper;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.util.ObjectMapperWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
//TODO unit test
public class RoleFilter extends OncePerRequestFilter {
    private final AccessTokenProvider accessTokenProvider;
    private final AntPathMatcher antPathMatcher;
    private final ErrorResponseFactory errorResponseFactory;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final RoleFilterSettingRegistry roleFilterSettingRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        List<RoleSetting> roleSettings = getMatchingSettings(request);
        log.info("Role settings: {}", roleSettings);

        if (roleSettings.isEmpty() || hasRequiredRoles(roleSettings)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("User does not have the required role.");
            if (RequestHelper.isRestCall(request)) {
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

        response.setStatus(errorResponse.getStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapperWrapper.writeValueAsString(errorResponse.getErrorResponse()));
        writer.flush();
        writer.close();
    }

    private List<RoleSetting> getMatchingSettings(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        List<RoleSetting> matchingRoles = roleFilterSettingRegistry.getSettings()
            .stream()
            .filter(roleSetting -> roleSetting.getMethods().stream().anyMatch(method -> method.equalsIgnoreCase(requestMethod)))
            .filter(roleSetting -> antPathMatcher.match(roleSetting.getPattern(), requestUri))
            .collect(Collectors.toList());
        log.info("Matching roles for {} - {}: {}", requestMethod, requestUri, matchingRoles);
        return matchingRoles;
    }

    private boolean hasRequiredRoles(List<RoleSetting> roleSettings) {
        Optional<AccessTokenHeader> accessTokenHeaderOptional = accessTokenProvider.getOptional();
        if (accessTokenHeaderOptional.isPresent()) {
            AccessTokenHeader accessTokenHeader = accessTokenHeaderOptional.get();
            log.info("Roles of user {}: {}", accessTokenHeader.getUserId(), accessTokenHeader.getRoles());
            return roleSettings.stream()
                .allMatch(roleSetting -> accessTokenHeader.getRoles().containsAll(roleSetting.getRequiredRoles()));
        } else {
            log.info("User is not logged in. Access denied.");
            return false;
        }
    }
}
