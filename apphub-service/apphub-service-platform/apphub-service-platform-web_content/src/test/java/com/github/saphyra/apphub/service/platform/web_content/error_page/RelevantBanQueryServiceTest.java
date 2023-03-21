package com.github.saphyra.apphub.service.platform.web_content.error_page;

import com.github.saphyra.apphub.api.user.client.BanClient;
import com.github.saphyra.apphub.api.user.model.response.BanDetailsResponse;
import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RelevantBanQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "role";
    private static final String ACCESS_TOKEN_HEADER = "access-token-header";
    private static final String LOCALE = "locale";

    @Mock
    private BanClient banClient;

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private RelevantBanQueryService underTest;

    @Mock
    private BanDetailsResponse relevantBan;

    @Mock
    private BanDetailsResponse irrelevantBan;

    @Mock
    private BanResponse banResponse;

    @Test
    void getRelevantBans() {
        given(accessTokenHeaderConverter.convertDomain(any(AccessTokenHeader.class))).willReturn(ACCESS_TOKEN_HEADER);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(banClient.getBans(USER_ID, ACCESS_TOKEN_HEADER, LOCALE)).willReturn(banResponse);
        given(banResponse.getBans()).willReturn(List.of(relevantBan, irrelevantBan));
        given(relevantBan.getBannedRole()).willReturn(ROLE);
        given(irrelevantBan.getBannedRole()).willReturn("asd");

        List<BanDetailsResponse> result = underTest.getRelevantBans(USER_ID, List.of(ROLE));

        assertThat(result).containsExactly(relevantBan);
    }
}