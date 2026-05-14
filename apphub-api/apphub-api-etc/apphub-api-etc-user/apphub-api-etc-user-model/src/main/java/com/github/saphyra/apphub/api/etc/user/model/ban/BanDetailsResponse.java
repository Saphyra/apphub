package com.github.saphyra.apphub.api.etc.user.model.ban;

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
public class BanDetailsResponse {
    private UUID id;
    private Role bannedRole;
    private String expiration;
    private Boolean permanent;
    private String reason;
    private UUID bannedById;
    private String bannedByUsername;
    private String bannedByEmail;
}
