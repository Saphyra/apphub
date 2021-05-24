package com.github.saphyra.apphub.service.skyxplore.game.service.chat.messaging;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChatMessageWebSocketEventHandlerTest {
    private static final UUID SENDER = UUID.randomUUID();
    private static final String SENDER_NAME = "sender-name";
    private static final String CHAT_ROOM = "chat-room";
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String MESSAGE = "message";

    @Mock
    private GameDao gameDao;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private ChatRoomMemberFilter chatRoomMemberFilter;

    @InjectMocks
    private ChatMessageWebSocketEventHandler underTest;

    @Mock
    private IncomingChatMessage incomingChatMessage;

    @Mock
    private WebSocketEvent event;

    @Mock
    private Game game;

    @Mock
    private Chat chat;

    @Mock
    private ChatRoom chatRoom;

    @Mock
    private SkyXploreCharacterModel characterModel;

    @Mock
    private Player player;

    @Test
    public void handle() {
        given(event.getPayload()).willReturn(incomingChatMessage);
        given(objectMapperWrapper.convertValue(incomingChatMessage, IncomingChatMessage.class)).willReturn(incomingChatMessage);
        given(gameDao.findByUserIdValidated(SENDER)).willReturn(game);
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(Arrays.asList(chatRoom));
        given(incomingChatMessage.getRoom()).willReturn(CHAT_ROOM);
        given(incomingChatMessage.getMessage()).willReturn(MESSAGE);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(PLAYER_ID, player));
        given(chatRoomMemberFilter.getMembers(SENDER, CHAT_ROOM, Arrays.asList(chatRoom), CollectionUtils.singleValueMap(PLAYER_ID, player))).willReturn(Arrays.asList(PLAYER_ID));
        given(characterProxy.getCharacterByUserId(SENDER)).willReturn(characterModel);
        given(characterModel.getName()).willReturn(SENDER_NAME);

        underTest.handle(SENDER, event);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToGame(argumentCaptor.capture());

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(PLAYER_ID);
        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE);
        assertThat(event.getPayload()).isInstanceOf(OutgoingChatMessage.class);
        OutgoingChatMessage outgoingChatMessage = (OutgoingChatMessage) event.getPayload();
        assertThat(outgoingChatMessage.getSenderId()).isEqualTo(SENDER);
        assertThat(outgoingChatMessage.getSenderName()).isEqualTo(SENDER_NAME);
        assertThat(outgoingChatMessage.getRoom()).isEqualTo(CHAT_ROOM);
        assertThat(outgoingChatMessage.getMessage()).isEqualTo(MESSAGE);
    }
}