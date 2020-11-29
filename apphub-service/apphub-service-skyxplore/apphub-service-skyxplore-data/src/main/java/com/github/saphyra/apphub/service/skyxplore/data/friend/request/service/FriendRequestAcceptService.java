package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service.FriendshipCreationService;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class FriendRequestAcceptService {
    private final FriendRequestDao friendRequestDao;
    private final FriendshipCreationService friendshipCreationService;

    @Transactional
    public void accept(UUID userId, UUID friendRequestId) {
        FriendRequest friendRequest = friendRequestDao.findById(friendRequestId)
            .orElseThrow(RuntimeException::new); //TODO proper exception

        if (!friendRequest.getFriendId().equals(userId)) {
            //TODO throw exception
        }

        friendshipCreationService.fromRequest(friendRequest);
        friendRequestDao.delete(friendRequest);
    }
}
