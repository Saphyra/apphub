package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreDataFriendApiClient;
import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SkyXploreDataProxyTest {
    private static final String ACCESS_TOKEN_HEADER = "access-token-header";
    private static final String LOCALE = "locale";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SkyXploreDataFriendApiClient dataFriendClient;

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private SkyXploreSavedGameClient savedGameClient;

    @InjectMocks
    private SkyXploreDataProxy underTest;

    @Mock
    private FriendshipResponse friendshipResponse;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private GameViewForLobbyCreation gameViewForLobbyCreation;

    @Test
    public void getFriends() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER);
        given(localeProvider.getOrDefault()).willReturn(LOCALE);
        given(dataFriendClient.getFriends(ACCESS_TOKEN_HEADER, LOCALE)).willReturn(Arrays.asList(friendshipResponse));

        List<FriendshipResponse> result = underTest.getFriends(accessTokenHeader);

        assertThat(result).containsExactly(friendshipResponse);
    }

    @Test
    public void getGameForLobbyCreation() {
        given(accessTokenProvider.getAsString()).willReturn(ACCESS_TOKEN_HEADER);
        given(localeProvider.getOrDefault()).willReturn(LOCALE);
        given(savedGameClient.getGameForLobbyCreation(GAME_ID, ACCESS_TOKEN_HEADER, LOCALE)).willReturn(gameViewForLobbyCreation);

        GameViewForLobbyCreation result = underTest.getGameForLobbyCreation(GAME_ID);

        assertThat(result).isEqualTo(gameViewForLobbyCreation);
    }
}