package com.github.saphyra.apphub.api.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class UserRoleResponse {
    @NonNull
    private final UUID userId;

    @NonNull
    private final String email;

    @NonNull
    private final String username;

    @NonNull
    private final List<String> roles;
}
