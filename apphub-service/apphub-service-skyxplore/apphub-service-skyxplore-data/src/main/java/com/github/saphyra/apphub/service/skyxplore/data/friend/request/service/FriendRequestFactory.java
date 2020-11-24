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
//TODO unit test
class FriendRequestFactory {
    private final IdGenerator idGenerator;

    FriendRequest create(UUID senderId, UUID friendId) {
        return FriendRequest.builder()
            .friendRequestId(idGenerator.randomUUID())
            .senderId(senderId)
            .friendId(friendId)
            .build();
    }
}
