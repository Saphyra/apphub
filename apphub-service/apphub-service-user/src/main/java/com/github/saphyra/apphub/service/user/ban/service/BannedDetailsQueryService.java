package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.ban.BannedDetailsResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BannedDetailsQueryService {
    private final BanDao banDao;

    public BannedDetailsResponse getBannedDetails(UUID userId, List<String> requiredRoles) {
        List<Ban> relevantBans = banDao.getByUserId(userId)
            .stream()
            .filter(ban -> requiredRoles.contains(ban.getBannedRole()))
            .toList();

        if (relevantBans.isEmpty()) {
            return BannedDetailsResponse.builder()
                .build();
        } else if (relevantBans.stream().anyMatch(Ban::getPermanent)) {
            return BannedDetailsResponse.builder()
                .permanent(true)
                .build();
        } else {
            LocalDateTime bannedUntil = relevantBans.stream()
                .max(Comparator.comparing(Ban::getExpiration))
                .map(Ban::getExpiration)
                .orElseThrow(() -> ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, userId + " is banned temporarily, without expiration set"));

            return BannedDetailsResponse.builder()
                .permanent(false)
                .bannedUntil(bannedUntil.toEpochSecond(ZoneOffset.UTC))
                .build();
        }
    }
}
