package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friend_request.FriendRequestResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.blacklist.dao.BlacklistDao;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequest;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendRequestCreationService {
    private final BlacklistDao blacklistDao;
    private final FriendRequestDao friendRequestDao;
    private final FriendshipDao friendshipDao;
    private final FriendRequestFactory friendRequestFactory;
    private final FriendRequestToResponseConverter friendRequestToResponseConverter;
    private final AccountClientProxy accountClientProxy;

    public FriendRequestResponse create(UUID userId, UUID receiverId) {
        if (!accountClientProxy.userExists(receiverId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "User not found with id " + receiverId);
        }

        if (blacklistDao.findByUserIdOrBlockedUserId(userId, receiverId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot send friendRequest to " + receiverId);
        }

        if (friendRequestDao.findBySenderIdAndReceiverId(userId, receiverId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "There is already a FriendRequest between " + userId + " and " + receiverId);
        }

        if (friendshipDao.findByUserIdAndFriendId(userId, receiverId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "There is already a Friendship between " + userId + " and " + receiverId);
        }

        FriendRequest friendRequest = friendRequestFactory.create(userId, receiverId);
        friendRequestDao.save(friendRequest);

        return friendRequestToResponseConverter.convert(friendRequest, receiverId);
    }
}
