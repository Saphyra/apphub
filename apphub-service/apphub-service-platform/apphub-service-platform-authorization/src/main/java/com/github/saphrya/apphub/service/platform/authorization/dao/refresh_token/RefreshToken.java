package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RefreshToken {
    private final UUID userId;
    private final UUID refreshTokenId;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiration;
    private final boolean rememberMe;
    private final String jwt;
}
