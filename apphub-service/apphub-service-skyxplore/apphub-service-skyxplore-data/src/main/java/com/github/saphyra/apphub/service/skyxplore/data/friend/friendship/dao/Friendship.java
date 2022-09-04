package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class Friendship {
    private final UUID friendshipId;
    private final UUID friend1;
    private final UUID friend2;

    public UUID getOtherId(UUID userId) {
        if (friend1.equals(userId)) {
            return friend2;
        }

        if (friend2.equals(userId)) {
            return friend1;
        }

        throw ExceptionFactory.invalidParam("userId", "Not related to friendship " + friendshipId);
    }
}
