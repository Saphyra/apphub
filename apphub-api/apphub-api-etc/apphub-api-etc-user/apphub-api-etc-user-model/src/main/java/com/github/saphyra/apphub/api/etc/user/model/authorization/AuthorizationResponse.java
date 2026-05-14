package com.github.saphyra.apphub.api.etc.user.model.authorization;

import com.github.saphyra.apphub.lib.common_domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AuthorizationResponse {
    private AuthorizationResult authorizationResult;
    private UUID userId;
    private List<Role> roles;
}
