package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.data.common.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
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
public class FriendRequestCancelServiceTest {
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();

    @Mock
    private FriendRequestDao friendRequestDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

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
        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToMainMenu(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_DELETED);
        assertThat(message.getRecipients()).containsExactlyInAnyOrder(SENDER_ID);
        assertThat(message.getEvent().getPayload()).isEqualTo(FRIEND_REQUEST_ID);
    }
}