package com.github.saphyra.apphub.integration.structure.api.community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GroupMemberResponse {
    private UUID groupMemberId;
    private UUID userId;
    private String username;
    private String email;
    private Boolean canModifyRoles;
    private Boolean canInvite;
    private Boolean canKick;
}
