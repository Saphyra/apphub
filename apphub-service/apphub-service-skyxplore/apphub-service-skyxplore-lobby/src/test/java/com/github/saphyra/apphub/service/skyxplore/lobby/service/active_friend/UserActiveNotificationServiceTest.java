package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserActiveNotificationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final String CHARACTER_NAME = "character-name";

    @Mock
    private SkyXploreDataProxy skyXploreDataProxy;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @Mock
    private LobbyDao lobbyDao;

    @InjectMocks
    private UserActiveNotificationService underTest;

    @Mock
    private FriendshipResponse friendshipResponse;

    @Mock
    private Lobby lobby;

    @Mock
    private SkyXploreCharacterModel characterModel;

    @BeforeEach
    void setUpRecipients() {
        given(skyXploreDataProxy.getFriends(any())).willReturn(List.of(friendshipResponse));
        given(friendshipResponse.getFriendId()).willReturn(FRIEND_ID);
        given(applicationContextProxy.getBean(LobbyDao.class)).willReturn(lobbyDao);
        given(lobbyDao.findByUserId(FRIEND_ID)).willReturn(Optional.of(lobby));
        given(characterProxy.getCharacter(USER_ID)).willReturn(characterModel);
        given(characterModel.getName()).willReturn(CHARACTER_NAME);
    }

    @AfterEach
    void verifyAccessToken() {
        ArgumentCaptor<AccessTokenHeader> argumentCaptor = ArgumentCaptor.forClass(AccessTokenHeader.class);
        then(skyXploreDataProxy).should().getFriends(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getUserId()).isEqualTo(USER_ID);
        assertThat(argumentCaptor.getValue().getRoles()).containsExactly("SKYXPLORE", "ACCESS");
    }

    @Test
    void userOnline() {
        underTest.userOnline(USER_ID);

        then(lobbyWebSocketHandler)
            .should()
            .sendEvent(
                List.of(FRIEND_ID),
                WebSocketEvent.builder()
                    .eventName(WebSocketEventName.SKYXPLORE_LOBBY_USER_ONLINE)
                    .payload(ActiveFriendResponse.builder()
                        .friendId(USER_ID)
                        .friendName(CHARACTER_NAME)
                        .build()
                    )
                    .build()
            );
    }

    @Test
    void userOffline() {
        underTest.userOffline(USER_ID);

        then(lobbyWebSocketHandler)
            .should()
            .sendEvent(
                List.of(FRIEND_ID),
                WebSocketEvent.builder()
                    .eventName(WebSocketEventName.SKYXPLORE_LOBBY_USER_OFFLINE)
                    .payload(ActiveFriendResponse.builder()
                        .friendName(CHARACTER_NAME)
                        .build()
                    )
                    .build()
            );
    }
}