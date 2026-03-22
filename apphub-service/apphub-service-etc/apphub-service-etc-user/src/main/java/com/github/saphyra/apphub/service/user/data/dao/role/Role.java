package com.github.saphyra.apphub.service.user.data.dao.role;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
public class Role {
    @NonNull
    private final UUID roleId;

    @NonNull
    private final UUID userId;

    @NonNull
    private final String role;
}
