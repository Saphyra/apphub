package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FriendshipFactory {
    private final IdGenerator idGenerator;

    Friendship create(UUID friendId, UUID senderId) {
        return Friendship.builder()
            .friendshipId(idGenerator.randomUuid())
            .friend1(friendId)
            .friend2(senderId)
            .build();
    }
}
