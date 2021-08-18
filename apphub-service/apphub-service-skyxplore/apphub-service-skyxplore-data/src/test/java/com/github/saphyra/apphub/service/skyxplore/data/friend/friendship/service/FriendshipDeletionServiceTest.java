package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.data.common.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.FriendshipDeletionPlayerProcessor;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FriendshipDeletionServiceTest {
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private FriendshipDeletionPlayerProcessor friendshipDeletionPlayerProcessor;

    @InjectMocks
    private FriendshipDeletionService underTest;

    @Mock
    private Friendship friendship;

    @Test
    public void friendshipNotFound() {
        given(friendshipDao.findById(FRIENDSHIP_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.removeFriendship(FRIENDSHIP_ID, USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.FRIENDSHIP_NOT_FOUND);
    }

    @Test
    public void forbiddenOperation() {
        given(friendshipDao.findById(FRIENDSHIP_ID)).willReturn(Optional.of(friendship));
        given(friendship.getFriend1()).willReturn(UUID.randomUUID());
        given(friendship.getFriend2()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.removeFriendship(FRIENDSHIP_ID, USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void removeFriendship() {
        given(friendshipDao.findById(FRIENDSHIP_ID)).willReturn(Optional.of(friendship));
        given(friendship.getFriend1()).willReturn(USER_ID);
        given(friendship.getFriend2()).willReturn(FRIEND_ID);

        underTest.removeFriendship(FRIENDSHIP_ID, USER_ID);

        verify(friendshipDao).delete(friendship);
        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToMainMenu(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED);
        assertThat(message.getRecipients()).containsExactlyInAnyOrder(FRIEND_ID, USER_ID);

        verify(friendshipDeletionPlayerProcessor).processDeletedFriendship(USER_ID, FRIEND_ID);
    }
}