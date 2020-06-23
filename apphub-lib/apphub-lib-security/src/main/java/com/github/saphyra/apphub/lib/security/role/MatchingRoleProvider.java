package com.github.saphyra.apphub.lib.security.role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

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
            .collect(Collectors.toList());
        log.info("Matching roles for {} - {}: {}", requestMethod, requestUri, matchingRoles);
        return matchingRoles;
    }
}
