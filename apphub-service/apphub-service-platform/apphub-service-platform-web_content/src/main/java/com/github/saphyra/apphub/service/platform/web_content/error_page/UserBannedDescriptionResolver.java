package com.github.saphyra.apphub.service.platform.web_content.error_page;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationClient;
import com.github.saphyra.apphub.api.user.client.BanClient;
import com.github.saphyra.apphub.api.user.model.response.BanDetailsResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.LocalizationKey;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserBannedDescriptionResolver {
    private final BanClient banClient;
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final LocaleProvider localeProvider;
    private final LocalizationClient localizationClient;

    public String resolve(UUID userId, String requiredRoles) {
        List<String> requiredRolesAsList = Arrays.asList(requiredRoles.split(","));
        if (requiredRolesAsList.isEmpty()) {
            return null;
        }

        AccessTokenHeader accessTokenHeader = createAccessToken();
        List<BanDetailsResponse> relevantBans = getRelevantBans(userId, requiredRolesAsList, accessTokenHeader);

        if (hasPermanentBan(relevantBans)) {
            return localizationClient.translateKey(LocalizationKey.USER_PERMANENTLY_BANNED, localeProvider.getLocaleValidated());
        } else if (!relevantBans.isEmpty()) {
            return localizationClient.translateKey(LocalizationKey.USER_BANNED, localeProvider.getLocaleValidated())
                .replace("{expiration}", getLatestBanExpiration(relevantBans));
        } else {
            return null;
        }
    }

    private AccessTokenHeader createAccessToken() {
        return AccessTokenHeader.builder()
            .accessTokenId(Constants.SERVICE_ID)
            .userId(Constants.SERVICE_ID)
            .roles(List.of("ADMIN", "ACCESS"))
            .build();
    }

    private List<BanDetailsResponse> getRelevantBans(UUID userId, List<String> requiredRolesAsList, AccessTokenHeader accessTokenHeader) {
        return banClient.getBans(userId, accessTokenHeaderConverter.convertDomain(accessTokenHeader), localeProvider.getLocaleValidated())
            .getBans()
            .stream()
            .filter(ban -> requiredRolesAsList.contains(ban.getBannedRole()))
            .collect(Collectors.toList());
    }

    private boolean hasPermanentBan(List<BanDetailsResponse> relevantBans) {
        return relevantBans.stream()
            .anyMatch(BanDetailsResponse::getPermanent);
    }

    private String getLatestBanExpiration(List<BanDetailsResponse> relevantBans) {
        return relevantBans.stream()
            .map(BanDetailsResponse::getExpiration)
            .sorted((o1, o2) -> Long.compare(o2, o1))
            .map(epochSeconds -> LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC))
            .findFirst()
            .map(LocalDateTime::toString)
            .orElse("");
    }
}
