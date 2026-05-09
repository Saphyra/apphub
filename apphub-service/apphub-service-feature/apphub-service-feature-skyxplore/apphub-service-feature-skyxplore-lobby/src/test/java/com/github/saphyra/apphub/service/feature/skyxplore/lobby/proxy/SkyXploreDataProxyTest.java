package com.github.saphyra.apphub.service.feature.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.feature.skyxplore.data.client.SkyXploreDataFriendApiClient;
import com.github.saphyra.apphub.api.feature.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.feature.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.feature.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SkyXploreDataProxyTest {
    private static final String ACCESS_TOKEN_HEADER = "access-token-header";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SkyXploreDataFriendApiClient dataFriendClient;

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private SkyXploreSavedGameClient savedGameClient;

    @InjectMocks
    private SkyXploreDataProxy underTest;

    @Mock
    private FriendshipResponse friendshipResponse;

    @Mock
    private AccessToken accessToken;

    @Mock
    private GameViewForLobbyCreation gameViewForLobbyCreation;

    @Test
    public void getFriends() {
        given(accessTokenHeaderConverter.convertDomain(accessToken)).willReturn(ACCESS_TOKEN_HEADER);
        given(dataFriendClient.getFriends(ACCESS_TOKEN_HEADER)).willReturn(List.of(friendshipResponse));

        List<FriendshipResponse> result = underTest.getFriends(accessToken);

        assertThat(result).containsExactly(friendshipResponse);
    }

    @Test
    public void getGameForLobbyCreation() {
        given(accessTokenProvider.getAsString()).willReturn(ACCESS_TOKEN_HEADER);
        given(savedGameClient.getGameForLobbyCreation(GAME_ID, ACCESS_TOKEN_HEADER)).willReturn(gameViewForLobbyCreation);

        GameViewForLobbyCreation result = underTest.getGameForLobbyCreation(GAME_ID);

        assertThat(result).isEqualTo(gameViewForLobbyCreation);
    }
}