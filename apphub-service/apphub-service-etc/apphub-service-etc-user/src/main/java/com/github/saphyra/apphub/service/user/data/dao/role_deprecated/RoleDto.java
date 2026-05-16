package com.github.saphyra.apphub.service.user.data.dao.role_deprecated;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
@Deprecated(forRemoval = true)
public class RoleDto {
    @NonNull
    private final UUID roleId;

    @NonNull
    private final UUID userId;

    @NonNull
    private final String role;
}
