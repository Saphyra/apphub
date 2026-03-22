package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.Friendship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FriendshipFactory {
    private final IdGenerator idGenerator;

    Friendship create(UUID userId, UUID friendId) {
        return Friendship.builder()
            .friendshipId(idGenerator.randomUuid())
            .userId(userId)
            .friendId(friendId)
            .build();
    }
}
