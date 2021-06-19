package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service.FriendshipCreationService;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendRequestAcceptService {
    private final FriendRequestDao friendRequestDao;
    private final FriendshipCreationService friendshipCreationService;

    @Transactional
    public void accept(UUID userId, UUID friendRequestId) {
        FriendRequest friendRequest = friendRequestDao.findById(friendRequestId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, "FriendRequest not found with id " + friendRequestId));

        if (!friendRequest.getFriendId().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " cannot accept friendRequest " + friendRequest);
        }

        friendshipCreationService.fromRequest(friendRequest);
        friendRequestDao.delete(friendRequest);
    }
}
