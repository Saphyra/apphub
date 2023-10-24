package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
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
public class FriendRequestCancelServiceTest {
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();

    @Mock
    private FriendRequestDao friendRequestDao;

    @Mock
    private SkyXploreFriendshipWebSocketHandler friendshipWebSocketHandler;

    @InjectMocks
    private FriendRequestCancelService underTest;

    @Mock
    private FriendRequest friendRequest;

    @Test
    public void friendRequestNotFound() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.cancelFriendRequest(USER_ID, FRIEND_REQUEST_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND);
    }

    @Test
    public void forbiddenOperation() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.of(friendRequest));
        given(friendRequest.getFriendId()).willReturn(UUID.randomUUID());
        given(friendRequest.getSenderId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.cancelFriendRequest(USER_ID, FRIEND_REQUEST_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void cancelFriendRequest() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.of(friendRequest));
        given(friendRequest.getFriendId()).willReturn(USER_ID);
        given(friendRequest.getOtherId(USER_ID)).willReturn(SENDER_ID);

        underTest.cancelFriendRequest(USER_ID, FRIEND_REQUEST_ID);

        verify(friendRequestDao).delete(friendRequest);

        then(friendshipWebSocketHandler).should().sendEvent(SENDER_ID, WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_DELETED, FRIEND_REQUEST_ID);
    }
}