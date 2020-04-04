package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.util.IdGenerator;
import com.github.saphyra.util.OffsetDateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class AccessTokenFactory {
    private final IdGenerator idGenerator;
    private final OffsetDateTimeProvider offsetDateTimeProvider;

    AccessToken create(UUID userId, boolean persistent) {
        return AccessToken.builder()
            .accessTokenId(idGenerator.randomUUID())
            .userId(userId)
            .persistent(persistent)
            .lastAccess(offsetDateTimeProvider.getCurrentDate())
            .build();
    }
}
