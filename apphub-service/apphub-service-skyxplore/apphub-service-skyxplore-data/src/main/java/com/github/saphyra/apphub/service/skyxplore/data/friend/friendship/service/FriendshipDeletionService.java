package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
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

    public void removeFriendship(UUID friendshipId, UUID userId) {
        Friendship friendship = friendshipDao.findById(friendshipId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.FRIENDSHIP_NOT_FOUND, "Friendshhip not found with id " + friendshipId));

        if (!friendship.getFriend1().equals(userId) && !friendship.getFriend2().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " cannot remove friendship " + friendshipId);
        }

        friendshipDao.delete(friendship);
    }
}
