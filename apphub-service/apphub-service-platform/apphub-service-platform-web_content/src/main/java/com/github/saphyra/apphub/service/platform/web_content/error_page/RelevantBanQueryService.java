package com.github.saphyra.apphub.service.platform.web_content.error_page;

import com.github.saphyra.apphub.api.user.client.BanClient;
import com.github.saphyra.apphub.api.user.model.response.BanDetailsResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class RelevantBanQueryService {
    private final BanClient banClient;
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final LocaleProvider localeProvider;

    List<BanDetailsResponse> getRelevantBans(UUID userId, List<String> requiredRolesAsList) {
        return banClient.getBans(userId, accessTokenHeaderConverter.convertDomain(createAccessToken()), localeProvider.getLocaleValidated())
            .getBans()
            .stream()
            .filter(ban -> requiredRolesAsList.contains(ban.getBannedRole()))
            .collect(Collectors.toList());
    }

    private AccessTokenHeader createAccessToken() {
        return AccessTokenHeader.builder()
            .build();
    }
}
