package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipCreationService {
    private final FriendshipFactory friendshipFactory;
    private final FriendshipDao friendshipDao;

    public void fromRequest(FriendRequest friendRequest) {
        Friendship friendship = friendshipFactory.create(friendRequest.getFriendId(), friendRequest.getSenderId());
        friendshipDao.save(friendship);
    }
}
