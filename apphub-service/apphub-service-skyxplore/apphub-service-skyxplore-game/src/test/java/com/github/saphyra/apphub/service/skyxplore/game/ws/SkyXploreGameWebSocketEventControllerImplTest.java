package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SkyXploreGameWebSocketEventControllerImplTest {
    private static final UUID FROM = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String ROOM_ID = "room-id";

    @Mock
    private GameDao gameDao;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private WebSocketEventHandler webSocketEventHandler;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    private SkyXploreGameWebSocketEventControllerImpl underTest;

    @Mock
    private WebSocketEvent webSocketEvent;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Mock
    private Chat chat;

    @Mock
    private ChatRoom chatRoom;

    @Mock
    private ChatRoom otherChatRoom;

    @Before
    public void setUp() {
        underTest = SkyXploreGameWebSocketEventControllerImpl.builder()
            .gameDao(gameDao)
            .characterProxy(characterProxy)
            .handlers(Arrays.asList(webSocketEventHandler))
            .messageSenderProxy(messageSenderProxy)
            .build();

        given(characterProxy.getCharacterByUserId(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(USERNAME).build());
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(Arrays.asList(chatRoom, otherChatRoom));
        given(chatRoom.getMembers()).willReturn(Arrays.asList(USER_ID, FROM));
        given(chatRoom.getId()).willReturn(ROOM_ID);
        given(otherChatRoom.getMembers()).willReturn(Collections.emptyList());
        given(game.filterConnectedPlayersFrom(Arrays.asList(USER_ID, FROM))).willReturn(Arrays.asList(USER_ID));
    }

    @Test
    public void processWebSocketEvent() {
        given(webSocketEvent.getEventName()).willReturn(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS);
        given(webSocketEventHandler.canHandle(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)).willReturn(true);

        underTest.processWebSocketEvent(FROM, webSocketEvent);

        verify(webSocketEventHandler).handle(FROM, webSocketEvent);
    }

    @Test
    public void userJoinedToGame() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));

        underTest.userJoinedToGame(USER_ID);

        verify(player).setConnected(true);

        verifyMessageSent(WebSocketEventName.SKYXPLORE_GAME_USER_JOINED);
    }

    @Test
    public void userLeftGame() {
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));

        underTest.userLeftGame(USER_ID);

        verify(player).setConnected(false);

        verifyMessageSent(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT);
    }

    private void verifyMessageSent(WebSocketEventName eventName) {
        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToGame(argumentCaptor.capture());

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(USER_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(eventName);

        SystemMessage systemMessage = (SystemMessage) event.getPayload();
        assertThat(systemMessage.getRoom()).isEqualTo(ROOM_ID);
        assertThat(systemMessage.getCharacterName()).isEqualTo(USERNAME);
        assertThat(systemMessage.getUserId()).isEqualTo(USER_ID);
    }
}