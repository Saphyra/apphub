package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
class RequiredRoleChecker {
    private final AccessTokenProvider accessTokenProvider;

    boolean hasRequiredRoles(List<RoleSetting> roleSettings) {
        Optional<AccessTokenHeader> accessTokenHeaderOptional = accessTokenProvider.getOptional();
        if (accessTokenHeaderOptional.isPresent()) {
            AccessTokenHeader accessTokenHeader = accessTokenHeaderOptional.get();
            log.info("Roles of user {}: {}", accessTokenHeader.getUserId(), accessTokenHeader.getRoles());

            List<RoleSetting> failedRoles = roleSettings.stream()
                .filter(roleSetting -> !accessTokenHeader.getRoles().containsAll(roleSetting.getRequiredRoles()))
                .collect(Collectors.toList());

            if (failedRoles.isEmpty()) {
                return true;
            } else {
                log.warn("{} does not have the required roles. Roles: {}, Failed settings: {}", accessTokenHeader.getUserId(), accessTokenHeader.getRoles(), failedRoles);
                return false;
            }
        } else {
            log.info("User is not logged in. Access denied.");
            return false;
        }
    }
}
