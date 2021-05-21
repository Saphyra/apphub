package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChatSendMessageWebSocketEventHandlerTest {
    private static final UUID FROM = UUID.randomUUID();
    private static final UUID MEMBER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "player-name";
    private static final Object EVENT_PAYLOAD = "event-payload";

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private ChatSendMessageWebSocketEventHandler underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private Member member;

    @Mock
    private SkyXploreCharacterModel characterModel;

    @Mock
    private WebSocketEvent receivedEvent;

    @Test
    public void canHandle_sendMessageEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE)).isTrue();
    }

    @Test
    public void canHandle_otherEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).isFalse();
    }

    @Test
    public void handle() {
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(MEMBER_ID, member));
        given(characterProxy.getCharacter(FROM)).willReturn(characterModel);
        given(characterModel.getName()).willReturn(PLAYER_NAME);
        given(receivedEvent.getPayload()).willReturn(EVENT_PAYLOAD);

        underTest.handle(FROM, receivedEvent);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(MEMBER_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE);

        ChatSendMessageWebSocketEventHandler.Message payload = (ChatSendMessageWebSocketEventHandler.Message) event.getPayload();
        assertThat(payload.getSenderId()).isEqualTo(FROM);
        assertThat(payload.getSenderName()).isEqualTo(PLAYER_NAME);
        assertThat(payload.getMessage()).isEqualTo(EVENT_PAYLOAD);
    }
}