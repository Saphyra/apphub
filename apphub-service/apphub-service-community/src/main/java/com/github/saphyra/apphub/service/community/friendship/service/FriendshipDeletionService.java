package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.Friendship;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipDeletionService {
    private final FriendshipDao friendshipDao;

    public void delete(UUID userId, UUID friendshipId) {
        Friendship friendship = friendshipDao.findById(friendshipId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Friendship not found with id" + friendshipId));

        if (!friendship.getUserId().equals(userId) && !friendship.getFriendId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not delete Friendship " + friendshipId);
        }

        friendshipDao.delete(friendship);
    }
}
