package com.github.saphyra.apphub.api.etc.user.model.ban;

import com.github.saphyra.apphub.lib.common_domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(exclude = "password")
@Builder
public class BanRequest {
    private UUID bannedUserId;
    private Role bannedRole;
    private Boolean permanent;
    private Integer duration;
    private String chronoUnit;
    private String reason;
    private String password;
}
