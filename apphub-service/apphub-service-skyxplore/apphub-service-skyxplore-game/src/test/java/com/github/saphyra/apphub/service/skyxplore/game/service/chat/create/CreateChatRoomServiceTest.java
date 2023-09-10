package com.github.saphyra.apphub.service.skyxplore.game.service.chat.create;

import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.ws.handler.SkyXploreGameWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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
    private SkyXploreGameWebSocketHandler webSocketHandler;

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

        then(webSocketHandler).should().sendEvent(List.of(USER_ID, MEMBER), WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED, new ChatRoomCreatedMessage(CHAT_ROOM_ID, ROOM_TITLE));
    }
}