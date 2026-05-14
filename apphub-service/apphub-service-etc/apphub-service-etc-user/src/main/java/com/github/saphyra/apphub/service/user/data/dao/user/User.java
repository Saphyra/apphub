package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_domain.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder(toBuilder = true)
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
    private LocalDateTime markedForDeletionAt;
    @NonNull
    @Builder.Default
    private Integer passwordFailureCount = 0;
    private LocalDateTime lockedUntil;
    @NonNull
    private List<Role> roles;

    public boolean isMarkedForDeletion() {
        return nonNull(markedForDeletionAt);
    }
}
