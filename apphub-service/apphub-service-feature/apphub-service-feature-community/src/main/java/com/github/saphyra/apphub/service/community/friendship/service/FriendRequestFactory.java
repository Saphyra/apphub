package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FriendRequestFactory {
    private final IdGenerator idGenerator;

    FriendRequest create(UUID senderId, UUID receiverId) {
        return FriendRequest.builder()
            .friendRequestId(idGenerator.randomUuid())
            .senderId(senderId)
            .receiverId(receiverId)
            .build();
    }
}
