package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FriendIdExtractor {
    public UUID getFriendId(Friendship friendship, UUID userId) {
        return friendship.getFriend1().equals(userId) ? friendship.getFriend2() : friendship.getFriend1();
    }
}
