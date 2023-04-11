package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ActiveFriendsServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID_1 = UUID.randomUUID();
    private static final UUID FRIEND_ID_2 = UUID.randomUUID();
    private static final String FRIEND_NAME = "friend-name";

    @Mock
    private ActiveUsersDao activeUsersDao;

    @Mock
    private SkyXploreDataProxy skyXploreDataProxy;

    @Mock
    private UserActiveNotificationService userActiveNotificationService;

    @Mock
    private LobbyDao lobbyDao;

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
        given(activeUsersDao.isOnline(FRIEND_ID_1)).willReturn(true);
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
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(FRIEND_ID_1, null));

        given(skyXploreDataProxy.getFriends(accessTokenHeader)).willReturn(Arrays.asList(activeFriend, inactiveFriend));
        given(activeFriend.getFriendId()).willReturn(FRIEND_ID_1);
        given(inactiveFriend.getFriendId()).willReturn(FRIEND_ID_2);
        given(activeUsersDao.isOnline(FRIEND_ID_1)).willReturn(true);
        given(activeUsersDao.isOnline(FRIEND_ID_2)).willReturn(true);
        given(activeFriend.getFriendName()).willReturn(FRIEND_NAME);

        List<ActiveFriendResponse> result = underTest.getActiveFriends(accessTokenHeader);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFriendId()).isEqualTo(FRIEND_ID_1);
        assertThat(result.get(0).getFriendName()).isEqualTo(FRIEND_NAME);
    }

    @Test
    public void playerOnline() {
        underTest.playerOnline(USER_ID);

        verify(activeUsersDao).playerOnline(USER_ID);
        verify(userActiveNotificationService).sendEvent(USER_ID, WebSocketEventName.SKYXPLORE_LOBBY_USER_ONLINE);
    }

    @Test
    public void playerOffline() {
        underTest.playerOffline(USER_ID);

        verify(activeUsersDao).playerOffline(USER_ID);
        verify(userActiveNotificationService).sendEvent(USER_ID, WebSocketEventName.SKYXPLORE_LOBBY_USER_OFFLINE);
    }
}