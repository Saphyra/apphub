package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class RedirectUrlProvider {
    String getRedirectUrl(List<RoleSetting> matchingSettings, AccessTokenHeader accessTokenHeader) {
        String missingRoles = matchingSettings.stream()
            .flatMap(roleSetting -> roleSetting.getRequiredRoles().stream())
            .distinct()
            .collect(Collectors.joining(","));
        return String.format(
            "%s?error_code=%s&user_id=%s&required_roles=%s",
            Endpoints.ERROR_PAGE,
            ErrorCode.MISSING_ROLE.name(),
            accessTokenHeader.getUserId(),
            missingRoles
        );
    }
}
