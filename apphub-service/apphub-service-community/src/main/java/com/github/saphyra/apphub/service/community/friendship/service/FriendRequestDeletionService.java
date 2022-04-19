package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
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
//TODO unit test
public class FriendRequestDeletionService {
    private final FriendRequestDao friendRequestDao;

    public void delete(UUID userId, UUID friendRequestId) {
        FriendRequest friendRequest = friendRequestDao.findById(friendRequestId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "FriendRequest not found with id " + friendRequestId));

        if (!friendRequest.getSenderId().equals(userId) && !friendRequest.getReceiverId().equals(userId)) {
            throw ExceptionFactory.loggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " must not delete FriendRequest " + friendRequestId);
        }

        friendRequestDao.delete(friendRequest);
    }
}
