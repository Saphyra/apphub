package com.github.saphyra.apphub.service.user.data.dao.user;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class User {
    @NonNull
    private final UUID userId;
    @NonNull
    private String email;
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String language;
    @Builder.Default
    private boolean markedForDeletion = false;
    private LocalDateTime markedForDeletionAt;
    @NonNull
    private Integer passwordFailureCount;
    private LocalDateTime lockedUntil;
}
