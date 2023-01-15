package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@Slf4j
@RequiredArgsConstructor
class MatchingRoleProvider {
    private final AntPathMatcher antPathMatcher;
    private final RoleFilterSettingRegistry roleFilterSettingRegistry;

    List<RoleSetting> getMatchingSettings(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        List<RoleSetting> matchingRoles = roleFilterSettingRegistry.getSettings()
            .stream()
            .filter(roleSetting -> roleSetting.getMethods().stream().anyMatch(method -> method.equalsIgnoreCase(requestMethod)))
            .filter(roleSetting -> antPathMatcher.match(roleSetting.getPattern(), requestUri))
            .filter(roleSetting -> !isWhiteListed(roleSetting.getWhitelistedEndpoints(), requestMethod, requestUri))
            .collect(Collectors.toList());
        log.info("Matching roles for {} - {}: {}", requestMethod, requestUri, matchingRoles);
        return matchingRoles;
    }

    private boolean isWhiteListed(List<WhiteListedEndpoint> whitelistedEndpoints, String requestMethod, String requestUri) {
        if (isNull(whitelistedEndpoints)) {
            return false;
        }

        return whitelistedEndpoints.stream()
            .filter(whiteListedEndpoint -> whiteListedEndpoint.getMethod().equals(requestMethod))
            .anyMatch(whiteListedEndpoint -> antPathMatcher.match(whiteListedEndpoint.getPattern(), requestUri));
    }
}
