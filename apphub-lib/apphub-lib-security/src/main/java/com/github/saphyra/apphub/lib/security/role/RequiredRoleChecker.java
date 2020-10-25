package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
            return roleSettings.stream()
                .allMatch(roleSetting -> accessTokenHeader.getRoles().containsAll(roleSetting.getRequiredRoles()));
        } else {
            log.info("User is not logged in. Access denied.");
            return false;
        }
    }
}
