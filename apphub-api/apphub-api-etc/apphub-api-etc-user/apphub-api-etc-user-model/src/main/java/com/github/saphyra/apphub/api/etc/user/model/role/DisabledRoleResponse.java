package com.github.saphyra.apphub.api.etc.user.model.role;

import com.github.saphyra.apphub.lib.common_domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DisabledRoleResponse {
    private Role role;
    private boolean disabled;
}
