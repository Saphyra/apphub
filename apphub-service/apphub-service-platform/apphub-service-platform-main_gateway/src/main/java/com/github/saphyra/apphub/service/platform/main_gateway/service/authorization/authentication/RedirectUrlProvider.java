package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class RedirectUrlProvider {
    String getRedirectUrl(Collection<Role> requiredRoles, AccessToken accessToken) {
        String roleNames = requiredRoles.stream()
            .map(Enum::name)
            .collect(Collectors.joining(","));
        return String.format(
            "%s?error_code=%s&user_id=%s&required_roles=%s",
            GenericEndpoints.ERROR_PAGE,
            ErrorCode.MISSING_ROLE.name(),
            accessToken.getUserId(),
            roleNames
        );
    }
}
