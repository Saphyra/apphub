package com.github.saphyra.apphub.api.etc.user.model.role;

import com.github.saphyra.apphub.lib.common_domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RoleRequest {
    private UUID userId;
    private Role role;
    private String password;
}
