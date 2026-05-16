package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class RedirectUrlProvider {
    String getRedirectUrl(List<RoleSetting> matchingSettings, AccessToken accessToken) {
        String missingRoles = matchingSettings.stream()
            .flatMap(roleSetting -> roleSetting.getRequiredRoles().stream())
            .distinct()
            .map(Enum::name)
            .collect(Collectors.joining(","));
        return String.format(
            "%s?error_code=%s&user_id=%s&required_roles=%s",
            GenericEndpoints.ERROR_PAGE,
            ErrorCode.MISSING_ROLE.name(),
            accessToken.getUserId(),
            missingRoles
        );
    }
}
