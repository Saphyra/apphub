package com.github.saphyra.apphub.service.skyxplore.game.service.chat;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.SkyXploreGameWebSocketHandler;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
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
    private SkyXploreGameWebSocketHandler webSocketHandler;

    @InjectMocks
    private LeaveChatRoomService underTest;

    @Mock
    private Game game;

    @Mock
    private Chat chat;

    @Mock
    private ChatRoom chatRoom;


    @Test
    public void leave_allianceRoom() {
        Throwable ex = catchThrowable(() -> underTest.leave(USER_ID, GameConstants.CHAT_ROOM_ALLIANCE));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void leave_generalRoom() {
        Throwable ex = catchThrowable(() -> underTest.leave(USER_ID, GameConstants.CHAT_ROOM_GENERAL));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void leave_chatRoomNotFound() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(CollectionUtils.toList(chatRoom));
        given(chatRoom.getId()).willReturn("asd");

        Throwable ex = catchThrowable(() -> underTest.leave(USER_ID, ROOM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void leave_notMemberOfRoom() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(CollectionUtils.toList(chatRoom));
        given(chatRoom.getId()).willReturn(ROOM_ID);
        given(chatRoom.getMembers()).willReturn(Collections.emptyList());

        underTest.leave(USER_ID, ROOM_ID);

        assertThat(chat.getRooms()).containsExactly(chatRoom);
        verifyNoInteractions(webSocketHandler);
    }

    @Test
    public void leave_noMoreMembers() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(CollectionUtils.toList(chatRoom));
        given(chatRoom.getId()).willReturn(ROOM_ID);
        given(chatRoom.getMembers()).willReturn(CollectionUtils.toList(USER_ID));

        underTest.leave(USER_ID, ROOM_ID);

        assertThat(chat.getRooms()).isEmpty();
        verifyNoInteractions(webSocketHandler);
    }

    @Test
    public void leave() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(CollectionUtils.toList(chatRoom));
        given(chatRoom.getId()).willReturn(ROOM_ID);
        given(chatRoom.getMembers()).willReturn(CollectionUtils.toList(USER_ID, ANOTHER_MEMBER));
        given(characterProxy.getCharacterByUserId(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build());

        underTest.leave(USER_ID, ROOM_ID);

        assertThat(chatRoom.getMembers()).containsExactly(ANOTHER_MEMBER);

        then(webSocketHandler).should().sendEvent(List.of(ANOTHER_MEMBER), WebSocketEventName.SKYXPLORE_GAME_USER_LEFT, new SystemMessage(ROOM_ID, CHARACTER_NAME, USER_ID));
    }
}