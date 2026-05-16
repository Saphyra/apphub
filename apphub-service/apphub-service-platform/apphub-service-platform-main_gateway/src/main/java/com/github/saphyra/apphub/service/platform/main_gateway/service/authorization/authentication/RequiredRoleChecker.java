package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
class RequiredRoleChecker {
    boolean hasRequiredRoles(List<RoleSetting> roleSettings, AccessToken accessToken) {
        log.debug("Roles of user {}: {}", accessToken.getUserId(), accessToken.getRoles());

        List<RoleSetting> failedRoles = roleSettings.stream()
            .filter(roleSetting -> !new HashSet<>(accessToken.getRoles()).containsAll(roleSetting.getRequiredRoles()))
            .collect(Collectors.toList());

        if (failedRoles.isEmpty()) {
            return true;
        } else {
            log.warn("{} does not have the required roles. Roles: {}, Failed settings: {}", accessToken.getUserId(), accessToken.getRoles(), failedRoles);
            return false;
        }
    }
}
