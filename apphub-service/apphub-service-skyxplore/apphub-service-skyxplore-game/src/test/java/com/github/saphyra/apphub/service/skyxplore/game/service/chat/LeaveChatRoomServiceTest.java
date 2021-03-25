package com.github.saphyra.apphub.service.skyxplore.game.service.chat;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.exception.ForbiddenException;
import com.github.saphyra.apphub.lib.exception.RestException;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class LeaveChatRoomServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROOM_ID = "room-id";
    private static final UUID ANOTHER_MEMBER = UUID.randomUUID();
    private static final String CHARACTER_NAME = "character-name";

    @Mock
    private GameDao gameDao;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private LeaveChatRoomService underTest;

    @Mock
    private Game game;

    @Mock
    private Chat chat;

    @Mock
    private ChatRoom chatRoom;

    @Before
    public void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(CollectionUtils.toList(chatRoom));
        given(chatRoom.getId()).willReturn(ROOM_ID);
    }

    @Test
    public void leave_allianceRoom() {
        Throwable ex = catchThrowable(() -> underTest.leave(USER_ID, GameConstants.CHAT_ROOM_ALLIANCE));

        assertThat(ex).isInstanceOf(ForbiddenException.class);
        ForbiddenException exception = (ForbiddenException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
    }

    @Test
    public void leave_generalRoom() {
        Throwable ex = catchThrowable(() -> underTest.leave(USER_ID, GameConstants.CHAT_ROOM_GENERAL));

        assertThat(ex).isInstanceOf(ForbiddenException.class);
        ForbiddenException exception = (ForbiddenException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
    }

    @Test
    public void leave_chatRoomNotFound() {
        given(chatRoom.getId()).willReturn("asd");

        Throwable ex = catchThrowable(() -> underTest.leave(USER_ID, ROOM_ID));

        assertThat(ex).isInstanceOf(RestException.class);
        RestException exception = (RestException) ex;
        assertThat(exception.getResponseStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void leave_notMemberOfRoom() {
        given(chatRoom.getMembers()).willReturn(Collections.emptyList());

        underTest.leave(USER_ID, ROOM_ID);

        assertThat(chat.getRooms()).containsExactly(chatRoom);
        verifyNoInteractions(messageSenderProxy);
    }

    @Test
    public void leave_noMoreMembers() {
        given(chatRoom.getMembers()).willReturn(CollectionUtils.toList(USER_ID));

        underTest.leave(USER_ID, ROOM_ID);

        assertThat(chat.getRooms()).isEmpty();
        verifyNoInteractions(messageSenderProxy);
    }

    @Test
    public void leave() {
        given(chatRoom.getMembers()).willReturn(CollectionUtils.toList(USER_ID, ANOTHER_MEMBER));
        given(characterProxy.getCharacterByUserId(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build());

        underTest.leave(USER_ID, ROOM_ID);

        assertThat(chatRoom.getMembers()).containsExactly(ANOTHER_MEMBER);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToGame(argumentCaptor.capture());

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(ANOTHER_MEMBER);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT);

        SystemMessage payload = (SystemMessage) event.getPayload();
        assertThat(payload.getRoom()).isEqualTo(ROOM_ID);
        assertThat(payload.getCharacterName()).isEqualTo(CHARACTER_NAME);
        assertThat(payload.getUserId()).isEqualTo(USER_ID);
    }
}