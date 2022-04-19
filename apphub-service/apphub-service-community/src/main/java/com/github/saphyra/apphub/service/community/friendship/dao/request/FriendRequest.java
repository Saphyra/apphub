package com.github.saphyra.apphub.service.community.friendship.dao.request;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Data
@Builder
public class FriendRequest {
    private final UUID friendRequestId;
    private final UUID senderId;
    private final UUID receiverId;

    public UUID getOtherUserId(UUID userId) {
        if (senderId.equals(userId)) {
            return receiverId;
        }

        if (receiverId.equals(userId)) {
            return senderId;
        }

        throw ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, friendRequestId + " does not have id " + userId);
    }
}
