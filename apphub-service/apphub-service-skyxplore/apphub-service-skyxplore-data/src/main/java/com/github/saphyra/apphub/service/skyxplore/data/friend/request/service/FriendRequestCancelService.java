package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ForbiddenException;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendRequestCancelService {
    private final FriendRequestDao friendRequestDao;

    public void cancelFriendRequest(UUID userId, UUID friendRequestId) {
        FriendRequest friendRequest = friendRequestDao.findById(friendRequestId)
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.FRIEND_REQUEST_NOT_FOUND.name()), "FriendRequest not found with id " + friendRequestId));

        if (!friendRequest.getFriendId().equals(userId) && !friendRequest.getSenderId().equals(userId)) {
            throw new ForbiddenException(new ErrorMessage(ErrorCode.FORBIDDEN_OPERATION.name()), userId + " must not cancel friendRequest " + friendRequestId);
        }

        friendRequestDao.delete(friendRequest);
    }
}
