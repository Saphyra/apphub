package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class AccessTokenFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;

    AccessToken create(UUID userId, boolean persistent) {
        return AccessToken.builder()
            .accessTokenId(idGenerator.randomUuid())
            .userId(userId)
            .persistent(persistent)
            .lastAccess(dateTimeUtil.getCurrentDateTime())
            .build();
    }
}
