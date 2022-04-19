package com.github.saphyra.apphub.service.community.blacklist.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Data
@Builder
//TODO unit test
public class Blacklist {
    private final UUID blacklistId;
    private final UUID userId;
    private final UUID blockedUserId;

    public UUID getOtherUserId(UUID userId) {
        if (this.userId.equals(userId)) {
            return blockedUserId;
        }

        if (blockedUserId.equals(userId)) {
            return this.userId;
        }

        throw ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, blacklistId + " does not have id " + userId);
    }
}
