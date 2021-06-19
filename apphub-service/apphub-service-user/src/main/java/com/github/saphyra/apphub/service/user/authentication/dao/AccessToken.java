package com.github.saphyra.apphub.service.user.authentication.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AccessToken {
    @NonNull
    private final UUID accessTokenId;

    @NonNull
    private final UUID userId;
    private final boolean persistent;

    @NonNull
    private LocalDateTime lastAccess;

    private String lastVisitedPage;
}
