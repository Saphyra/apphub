package com.github.saphyra.apphub.service.skyxplore.game.service.chat.messaging;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ChatMessageWebSocketEventHandlerTest {
    private static final UUID SENDER = UUID.randomUUID();
    private static final String SENDER_NAME = "sender-name";
    private static final String CHAT_ROOM = "chat-room";
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String MESSAGE = "message";

    @Mock
    private GameDao gameDao;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private SkyXploreGameMainWebSocketHandler webSocketHandler;

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
    void tooLongMessage(){
        given(event.getPayload()).willReturn(incomingChatMessage);
        given(objectMapper.convertValue(incomingChatMessage, IncomingChatMessage.class)).willReturn(incomingChatMessage);
        given(incomingChatMessage.getMessage()).willReturn(Stream.generate(() -> "a").limit(1025).collect(Collectors.joining()));

        ExceptionValidator.validateInvalidParam(() -> underTest.handle(SENDER, event, webSocketHandler), "message", "too long");
    }

    @Test
    public void handle() {
        given(event.getPayload()).willReturn(incomingChatMessage);
        given(objectMapper.convertValue(incomingChatMessage, IncomingChatMessage.class)).willReturn(incomingChatMessage);
        given(gameDao.findByUserIdValidated(SENDER)).willReturn(game);
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(List.of(chatRoom));
        given(incomingChatMessage.getRoom()).willReturn(CHAT_ROOM);
        given(incomingChatMessage.getMessage()).willReturn(MESSAGE);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(PLAYER_ID, player));
        given(chatRoomMemberFilter.getMembers(SENDER, CHAT_ROOM, List.of(chatRoom), CollectionUtils.singleValueMap(PLAYER_ID, player))).willReturn(List.of(PLAYER_ID));
        given(characterProxy.getCharacterByUserId(SENDER)).willReturn(characterModel);
        given(characterModel.getName()).willReturn(SENDER_NAME);

        underTest.handle(SENDER, event, webSocketHandler);

        ArgumentCaptor<WebSocketEvent> argumentCaptor = ArgumentCaptor.forClass(WebSocketEvent.class);
        then(webSocketHandler).should().sendEvent(eq(List.of(PLAYER_ID)), argumentCaptor.capture());

        WebSocketEvent event = argumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE);
        assertThat(event.getPayload()).isInstanceOf(OutgoingChatMessage.class);
        OutgoingChatMessage outgoingChatMessage = (OutgoingChatMessage) event.getPayload();
        assertThat(outgoingChatMessage.getSenderId()).isEqualTo(SENDER);
        assertThat(outgoingChatMessage.getSenderName()).isEqualTo(SENDER_NAME);
        assertThat(outgoingChatMessage.getRoom()).isEqualTo(CHAT_ROOM);
        assertThat(outgoingChatMessage.getMessage()).isEqualTo(MESSAGE);
    }
}