package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.request.BanRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
class BanFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;

    Ban create(UUID userId, BanRequest request) {
        LocalDateTime expiration = null;
        if (!request.getPermanent()) {
            expiration = dateTimeUtil.getCurrentDate().plus(request.getDuration(), ChronoUnit.valueOf(request.getChronoUnit()));
        }

        return Ban.builder()
            .id(idGenerator.randomUuid())
            .userId(request.getBannedUserId())
            .bannedRole(request.getBannedRole())
            .expiration(expiration)
            .permanent(request.getPermanent())
            .reason(request.getReason())
            .bannedBy(userId)
            .build();
    }
}
