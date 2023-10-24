package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.FriendshipDeletionPlayerProcessor;
import com.github.saphyra.apphub.service.skyxplore.data.ws.SkyXploreFriendshipWebSocketHandler;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FriendshipDeletionServiceTest {
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private SkyXploreFriendshipWebSocketHandler friendshipWebSocketHandler;

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
        given(friendship.getOtherId(USER_ID)).willReturn(FRIEND_ID);

        underTest.removeFriendship(FRIENDSHIP_ID, USER_ID);

        verify(friendshipDao).delete(friendship);

        then(friendshipWebSocketHandler).should().sendEvent(FRIEND_ID, WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED, FRIENDSHIP_ID);

        verify(friendshipDeletionPlayerProcessor).processDeletedFriendship(USER_ID, FRIEND_ID);
    }
}