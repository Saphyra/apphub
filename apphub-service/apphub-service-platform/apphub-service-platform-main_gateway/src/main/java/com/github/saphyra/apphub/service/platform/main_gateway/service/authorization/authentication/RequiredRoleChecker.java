package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
class RequiredRoleChecker {
    private final DisabledRoleCache disabledRoleCache;

    Mono<Boolean> hasRequiredRoles(Set<Role> requiredRoles, AccessToken accessToken) {
        return disabledRoleCache.get()
            .doOnNext(disabledRoles -> log.debug("Required roles: {}, Disabled roles: {}, User {} has roles: {}", requiredRoles, disabledRoles, accessToken.getUserId(), accessToken.getRoles()))
            .map(disabledRoles -> requiredRoles.stream()
                .filter(role -> !accessToken.getRoles().contains(role) || disabledRoles.contains(role))
                .collect(Collectors.toSet()))
            .map(failedRoles -> {
                if (failedRoles.isEmpty()) {
                    return true;
                } else {
                    log.warn("{} does not have the required roles. Roles: {}, Missing roles: {}", accessToken.getUserId(), accessToken.getRoles(), failedRoles);
                    return false;
                }
            });
    }
}
