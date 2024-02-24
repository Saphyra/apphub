package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
class RequiredRoleChecker {
    boolean hasRequiredRoles(List<RoleSetting> roleSettings, AccessTokenHeader accessTokenHeader) {
        log.debug("Roles of user {}: {}", accessTokenHeader.getUserId(), accessTokenHeader.getRoles());

        List<RoleSetting> failedRoles = roleSettings.stream()
            .filter(roleSetting -> !accessTokenHeader.getRoles().containsAll(roleSetting.getRequiredRoles()))
            .collect(Collectors.toList());

        if (failedRoles.isEmpty()) {
            return true;
        } else {
            log.warn("{} does not have the required roles. Roles: {}, Failed settings: {}", accessTokenHeader.getUserId(), accessTokenHeader.getRoles(), failedRoles);
            return false;
        }
    }
}
