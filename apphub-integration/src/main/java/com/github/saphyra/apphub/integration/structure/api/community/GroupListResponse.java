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
public class GroupListResponse {
    private UUID groupId;
    private String name;
    private UUID ownerId;
    private GroupInvitationType invitationType;
}
