package com.github.saphyra.apphub.service.platform.web_content.error_page;

import com.github.saphyra.apphub.api.user.model.response.BanDetailsResponse;
import com.github.saphyra.apphub.lib.common_domain.LocalizationKey;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.platform.web_content.error_code.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class UserBannedDescriptionResolver {
    private final RelevantBanQueryService relevantBanQueryService;
    private final LocaleProvider localeProvider;
    private final TranslationService translationService;

    String resolve(UUID userId, String requiredRoles) {
        List<String> requiredRolesAsList = Arrays.asList(requiredRoles.split(","));
        if (requiredRolesAsList.isEmpty()) {
            return null;
        }

        List<BanDetailsResponse> relevantBans = relevantBanQueryService.getRelevantBans(userId, requiredRolesAsList);

        if (hasPermanentBan(relevantBans)) {
            return translationService.translate(LocalizationKey.USER_PERMANENTLY_BANNED, localeProvider.getLocaleValidated());
        } else if (!relevantBans.isEmpty()) {
            return translationService.translate(LocalizationKey.USER_BANNED, localeProvider.getLocaleValidated())
                .replace("{expiration}", getLatestBanExpiration(relevantBans));
        } else {
            return null;
        }
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
