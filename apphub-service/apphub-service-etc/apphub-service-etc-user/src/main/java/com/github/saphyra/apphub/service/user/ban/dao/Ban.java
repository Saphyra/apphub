package com.github.saphyra.apphub.service.user.ban.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class Ban {
    private final UUID id;
    private final UUID userId;
    private final String bannedRole;
    private final LocalDateTime expiration;
    private final Boolean permanent;
    private final String reason;
    private final UUID bannedBy;
}
