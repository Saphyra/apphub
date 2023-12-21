package com.github.saphyra.apphub.service.platform.web_content.error_page;

import com.github.saphyra.apphub.api.user.model.response.BanDetailsResponse;
import com.github.saphyra.apphub.lib.common_domain.LocalizationKey;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.platform.web_content.error_code.TranslationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserBannedDescriptionResolverTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE_1 = "role-1";
    private static final String ROLE_2 = "role-2";
    private static final String REQUIRED_ROLES = String.format("%s,%s", ROLE_1, ROLE_2);
    private static final String LOCALE = "locale";
    private static final String RESOLVED_LOCALIZATION = "resolved-{expiration}localization";
    private static final String EXPIRATION = "expiration";
    @Mock
    private RelevantBanQueryService relevantBanQueryService;

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private TranslationService translationService;

    @InjectMocks
    private UserBannedDescriptionResolver underTest;

    @Mock
    private BanDetailsResponse ban;

    @Test
    void userPermanentlyBanned() {
        given(relevantBanQueryService.getRelevantBans(USER_ID, List.of(ROLE_1, ROLE_2))).willReturn(List.of(ban));
        given(ban.getPermanent()).willReturn(true);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(translationService.translate(LocalizationKey.USER_PERMANENTLY_BANNED, LOCALE)).willReturn(RESOLVED_LOCALIZATION);

        String result = underTest.resolve(USER_ID, REQUIRED_ROLES);

        assertThat(result).isEqualTo(RESOLVED_LOCALIZATION);
    }

    @Test
    void userBanned() {
        given(relevantBanQueryService.getRelevantBans(USER_ID, List.of(ROLE_1, ROLE_2))).willReturn(List.of(ban));
        given(ban.getPermanent()).willReturn(false);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(ban.getExpiration()).willReturn(EXPIRATION);
        given(translationService.translate(LocalizationKey.USER_BANNED, LOCALE)).willReturn(RESOLVED_LOCALIZATION);

        String result = underTest.resolve(USER_ID, REQUIRED_ROLES);

        assertThat(result).isEqualTo(RESOLVED_LOCALIZATION.replace("{expiration}", EXPIRATION));
    }

    @Test
    void noRelevantBan() {
        given(relevantBanQueryService.getRelevantBans(USER_ID, List.of(ROLE_1, ROLE_2))).willReturn(Collections.emptyList());

        String result = underTest.resolve(USER_ID, REQUIRED_ROLES);

        assertThat(result).isNull();
    }
}