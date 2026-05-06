package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RefreshToken {
    @NonNull
    private final UUID userId;
    @NonNull
    private final UUID refreshTokenId;
    @NonNull
    private final LocalDateTime issuedAt;
    @NonNull
    private final LocalDateTime expiration;
    private final boolean rememberMe;
}
