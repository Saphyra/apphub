package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class BlacklistFactory {
    private final IdGenerator idGenerator;

    Blacklist create(UUID userId, UUID blockedUserId) {
        return Blacklist.builder()
            .blacklistId(idGenerator.randomUuid())
            .userId(userId)
            .blockedUserId(blockedUserId)
            .build();
    }
}
