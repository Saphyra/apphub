package com.github.saphyra.apphub.service.community.friendship.dao.friend;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Data
@Builder
public class Friendship {
    private final UUID friendshipId;
    private final UUID userId;
    private final UUID friendId;

    public UUID getOtherUserId(UUID userId) {
        if (this.userId.equals(userId)) {
            return friendId;
        }

        if (friendId.equals(userId)) {
            return this.userId;
        }

        throw ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, friendshipId + " does not have id " + userId);
    }
}
