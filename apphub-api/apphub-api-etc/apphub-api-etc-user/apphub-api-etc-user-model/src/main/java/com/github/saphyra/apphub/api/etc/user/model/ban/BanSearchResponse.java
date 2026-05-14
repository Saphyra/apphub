package com.github.saphyra.apphub.api.etc.user.model.ban;

import com.github.saphyra.apphub.lib.common_domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BanSearchResponse {
    private UUID userId;
    private String username;
    private String email;
    private List<Role> bannedRoles;
    private Boolean markedForDeletion;
}
