package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.common.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendRequestCancelService {
    private final FriendRequestDao friendRequestDao;
    private final MessageSenderProxy messageSenderProxy;

    public void cancelFriendRequest(UUID userId, UUID friendRequestId) {
        FriendRequest friendRequest = friendRequestDao.findById(friendRequestId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, "FriendRequest not found with id " + friendRequestId));

        if (!friendRequest.getFriendId().equals(userId) && !friendRequest.getSenderId().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " must not cancel friendRequest " + friendRequestId);
        }

        friendRequestDao.delete(friendRequest);

        WebSocketMessage message = WebSocketMessage.forEventAndRecipients(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_DELETED, Arrays.asList(friendRequest.getSenderId(), friendRequest.getFriendId()));
        messageSenderProxy.sendToMainMenu(message);
    }
}
