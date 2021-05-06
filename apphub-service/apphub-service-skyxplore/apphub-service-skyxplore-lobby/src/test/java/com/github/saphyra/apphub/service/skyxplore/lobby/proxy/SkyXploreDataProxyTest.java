package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreDataFriendApiClient;
import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SkyXploreDataProxyTest {
    private static final String ACCESS_TOKEN_HEADER = "access-token-header";
    private static final String LOCALE = "locale";

    @Mock
    private SkyXploreDataFriendApiClient dataFriendClient;

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private SkyXploreDataProxy underTest;

    @Mock
    private FriendshipResponse friendshipResponse;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void getFriends() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(dataFriendClient.getFriends(ACCESS_TOKEN_HEADER, LOCALE)).willReturn(Arrays.asList(friendshipResponse));

        List<FriendshipResponse> result = underTest.getFriends(accessTokenHeader);

        assertThat(result).containsExactly(friendshipResponse);
    }
}