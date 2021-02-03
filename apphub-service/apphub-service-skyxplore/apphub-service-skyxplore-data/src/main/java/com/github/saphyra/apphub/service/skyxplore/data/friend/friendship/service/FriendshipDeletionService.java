package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ForbiddenException;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipDeletionService {
    private final FriendshipDao friendshipDao;

    public void removeFriendship(UUID friendshipId, UUID userId) {
        Friendship friendship = friendshipDao.findById(friendshipId)
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.FRIENDSHIP_NOT_FOUND.name()), "Friendship not found with id " + friendshipId));

        if (!friendship.getFriend1().equals(userId) && !friendship.getFriend2().equals(userId)) {
            throw new ForbiddenException(new ErrorMessage(ErrorCode.FORBIDDEN_OPERATION.name()), userId + " cannot remove friendship " + friendshipId);
        }

        friendshipDao.delete(friendship);
    }
}
