package com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@Builder
public class FriendRequest {
    private final UUID friendRequestId;
    private final UUID senderId;
    private final UUID friendId;

    public UUID getOtherId(UUID userId) {
        if (senderId.equals(userId)) {
            return friendId;
        }

        if (friendId.equals(userId)) {
            return senderId;
        }

        throw ExceptionFactory.invalidParam("userId", "Not related to friendRequest " + friendRequestId);
    }
}
