package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.ActiveFriendResponse;
import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserActiveNotificationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID_1 = UUID.randomUUID();
    private static final UUID FRIEND_ID_2 = UUID.randomUUID();
    private static final String PLAYER_NAME = "player-name";

    @Mock
    private SkyXploreDataProxy skyXploreDataProxy;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private UserActiveNotificationService underTest;

    @Mock
    private FriendshipResponse friendInLobby;

    @Mock
    private FriendshipResponse otherFriend;

    @Mock
    private Lobby lobby;

    @Mock
    private SkyXploreCharacterModel characterModel;

    @Test
    public void sendEvent() {
        given(skyXploreDataProxy.getFriends(any(AccessTokenHeader.class))).willReturn(Arrays.asList(friendInLobby, otherFriend));
        given(friendInLobby.getFriendId()).willReturn(FRIEND_ID_1);
        given(otherFriend.getFriendId()).willReturn(FRIEND_ID_2);
        given(lobbyDao.findByUserId(FRIEND_ID_1)).willReturn(Optional.of(lobby));
        given(characterProxy.getCharacter(USER_ID)).willReturn(characterModel);
        given(characterModel.getName()).willReturn(PLAYER_NAME);

        underTest.sendEvent(USER_ID, WebSocketEventName.SKYXPLORE_LOBBY_USER_ONLINE);

        ArgumentCaptor<AccessTokenHeader> accessTokenHeaderArgumentCaptor = ArgumentCaptor.forClass(AccessTokenHeader.class);
        verify(skyXploreDataProxy).getFriends(accessTokenHeaderArgumentCaptor.capture());
        AccessTokenHeader accessTokenHeader = accessTokenHeaderArgumentCaptor.getValue();
        assertThat(accessTokenHeader.getUserId()).isEqualTo(USER_ID);
        assertThat(accessTokenHeader.getRoles()).containsExactlyInAnyOrder("SKYXPLORE", "ACCESS");

        ArgumentCaptor<WebSocketMessage> webSocketMessageArgumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(webSocketMessageArgumentCaptor.capture());
        WebSocketMessage message = webSocketMessageArgumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(FRIEND_ID_1);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_USER_ONLINE);

        ActiveFriendResponse activeFriendResponse = (ActiveFriendResponse) event.getPayload();
        assertThat(activeFriendResponse.getFriendId()).isEqualTo(USER_ID);
        assertThat(activeFriendResponse.getFriendName()).isEqualTo(PLAYER_NAME);
    }
}