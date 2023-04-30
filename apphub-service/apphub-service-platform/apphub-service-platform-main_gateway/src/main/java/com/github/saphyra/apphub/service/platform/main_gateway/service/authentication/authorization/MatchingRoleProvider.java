package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
    private final RoleProperties roleProperties;

    List<RoleSetting> getMatchingSettings(ServerHttpRequest request) {
        String requestUri = request.getURI()
            .getPath();
        HttpMethod requestMethod = request.getMethod();
        List<RoleSetting> matchingRoles = roleProperties.getSettings()
            .stream()
            .filter(roleSetting -> roleSetting.getMethods().stream().anyMatch(method -> method.equalsIgnoreCase(requestMethod.name())))
            .filter(roleSetting -> antPathMatcher.match(roleSetting.getPattern(), requestUri))
            .filter(roleSetting -> !isWhiteListed(roleSetting.getWhitelistedEndpoints(), requestMethod, requestUri))
            .collect(Collectors.toList());
        log.info("Matching roles for {} - {}: {}", requestMethod, requestUri, matchingRoles);
        return matchingRoles;
    }

    private boolean isWhiteListed(List<WhiteListedEndpoint> whitelistedEndpoints, HttpMethod requestMethod, String requestUri) {
        if (isNull(whitelistedEndpoints)) {
            return false;
        }

        return whitelistedEndpoints.stream()
            .filter(whiteListedEndpoint -> whiteListedEndpoint.getMethod().equalsIgnoreCase(requestMethod.name()))
            .anyMatch(whiteListedEndpoint -> antPathMatcher.match(whiteListedEndpoint.getPattern(), requestUri));
    }
}
