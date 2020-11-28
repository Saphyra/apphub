package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class FriendRequestCancelService {
    private final FriendRequestDao friendRequestDao;

    public void cancelFriendRequest(UUID userId, UUID friendRequestId) {
        FriendRequest friendRequest = friendRequestDao.findById(friendRequestId)
            .orElseThrow(RuntimeException::new); //TODO proper exception

        if (!friendRequest.getFriendId().equals(userId) && !friendRequest.getSenderId().equals(userId)) {
            //TODO throw exception
        }

        friendRequestDao.delete(friendRequest);
    }
}
