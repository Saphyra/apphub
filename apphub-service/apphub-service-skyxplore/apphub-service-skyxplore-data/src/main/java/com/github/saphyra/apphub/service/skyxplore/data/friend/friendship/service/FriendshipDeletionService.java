package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class FriendshipDeletionService {
    private final FriendshipDao friendshipDao;

    public void removeFriendship(UUID friendshipId, UUID userId) {
        Friendship friendship = friendshipDao.findById(friendshipId)
            .orElseThrow(RuntimeException::new); //TODO proper exception

        if (!friendship.getFriend1().equals(userId) && !friendship.getFriend2().equals(userId)) {
            //TODO throw exception
        }

        friendshipDao.delete(friendship);
    }
}
