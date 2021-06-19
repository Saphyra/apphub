package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FriendRequestFactory {
    private final IdGenerator idGenerator;

    FriendRequest create(UUID senderId, UUID friendId) {
        return FriendRequest.builder()
            .friendRequestId(idGenerator.randomUuid())
            .senderId(senderId)
            .friendId(friendId)
            .build();
    }
}
