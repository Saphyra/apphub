package com.github.saphyra.apphub.api.community.model.response.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GroupMemberRoleRequest {
    private Boolean canInvite;
    private Boolean canKick;
    private Boolean canModifyRoles;
}
