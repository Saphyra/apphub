package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyInvitationWebSocketHandler;
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
public class ActiveFriendsServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID_1 = UUID.randomUUID();
    private static final UUID FRIEND_ID_2 = UUID.randomUUID();
    private static final String FRIEND_NAME = "friend-name";

    @Mock
    private SkyXploreDataProxy skyXploreDataProxy;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private SkyXploreLobbyInvitationWebSocketHandler invitationWebSocketHandler;

    @InjectMocks
    private ActiveFriendsService underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private FriendshipResponse activeFriend;

    @Mock
    private FriendshipResponse inactiveFriend;

    @Mock
    private Lobby lobby;

    @Test
    public void getActiveFriends_newGame() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getType()).willReturn(LobbyType.NEW_GAME);

        given(skyXploreDataProxy.getFriends(accessTokenHeader)).willReturn(Arrays.asList(activeFriend, inactiveFriend));
        given(activeFriend.getFriendId()).willReturn(FRIEND_ID_1);
        given(inactiveFriend.getFriendId()).willReturn(FRIEND_ID_2);
        given(invitationWebSocketHandler.isConnected(FRIEND_ID_1)).willReturn(true);
        given(activeFriend.getFriendName()).willReturn(FRIEND_NAME);

        List<ActiveFriendResponse> result = underTest.getActiveFriends(accessTokenHeader);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFriendId()).isEqualTo(FRIEND_ID_1);
        assertThat(result.get(0).getFriendName()).isEqualTo(FRIEND_NAME);
    }

    @Test
    public void getActiveFriends_loadGame() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getType()).willReturn(LobbyType.LOAD_GAME);
        given(lobby.getExpectedPlayers()).willReturn(List.of(FRIEND_ID_1));

        given(skyXploreDataProxy.getFriends(accessTokenHeader)).willReturn(Arrays.asList(activeFriend, inactiveFriend));
        given(activeFriend.getFriendId()).willReturn(FRIEND_ID_1);
        given(inactiveFriend.getFriendId()).willReturn(FRIEND_ID_2);
        given(invitationWebSocketHandler.isConnected(FRIEND_ID_1)).willReturn(true);
        given(invitationWebSocketHandler.isConnected(FRIEND_ID_2)).willReturn(true);
        given(activeFriend.getFriendName()).willReturn(FRIEND_NAME);

        List<ActiveFriendResponse> result = underTest.getActiveFriends(accessTokenHeader);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFriendId()).isEqualTo(FRIEND_ID_1);
        assertThat(result.get(0).getFriendName()).isEqualTo(FRIEND_NAME);
    }
}