package com.github.saphyra.apphub.service.skyxplore.game.service.chat.create;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
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
public class CreateChatRoomServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID MEMBER = UUID.randomUUID();
    private static final String ROOM_TITLE = "room-title";
    private static final String CHAT_ROOM_ID = "chat-room-id";

    @Mock
    private ChatRoomFactory chatRoomFactory;

    @Mock
    private GameDao gameDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private CreateChatRoomRequestValidator createChatRoomRequestValidator;

    @InjectMocks
    private CreateChatRoomService underTest;

    @Mock
    private Game game;

    @Mock
    private Chat chat;

    @Mock
    private ChatRoom chatRoom;

    @Test
    public void createChatRoom() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .members(Arrays.asList(MEMBER))
            .roomTitle(ROOM_TITLE)
            .build();

        given(chatRoomFactory.create(USER_ID, Arrays.asList(MEMBER))).willReturn(chatRoom);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getChat()).willReturn(chat);
        given(chatRoom.getId()).willReturn(CHAT_ROOM_ID);
        given(chatRoom.getMembers()).willReturn(Arrays.asList(USER_ID, MEMBER));

        underTest.createChatRoom(USER_ID, request);

        verify(createChatRoomRequestValidator).validate(request, game);
        verify(chat).addRoom(chatRoom);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToGame(argumentCaptor.capture());

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactlyInAnyOrder(USER_ID, MEMBER);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED);

        ChatRoomCreatedMessage payload = (ChatRoomCreatedMessage) event.getPayload();
        assertThat(payload.getId()).isEqualTo(CHAT_ROOM_ID);
        assertThat(payload.getTitle()).isEqualTo(ROOM_TITLE);
    }
}