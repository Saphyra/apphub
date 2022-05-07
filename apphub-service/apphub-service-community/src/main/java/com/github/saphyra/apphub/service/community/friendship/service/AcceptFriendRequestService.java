package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.Friendship;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequest;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcceptFriendRequestService {
    private final FriendRequestDao friendRequestDao;
    private final FriendshipFactory friendshipFactory;
    private final FriendshipDao friendshipDao;
    private final FriendshipToResponseConverter friendshipToResponseConverter;

    @Transactional
    public FriendshipResponse accept(UUID userId, UUID friendRequestId) {
        FriendRequest friendRequest = friendRequestDao.findById(friendRequestId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "FriendRequest not found with id " + friendRequestId));

        if (!friendRequest.getReceiverId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not accept FriendRequest " + friendRequestId);
        }

        Friendship friendship = friendshipFactory.create(friendRequest.getSenderId(), userId);
        friendshipDao.save(friendship);

        friendRequestDao.delete(friendRequest);

        return friendshipToResponseConverter.convert(friendship, friendRequest.getSenderId());
    }
}
