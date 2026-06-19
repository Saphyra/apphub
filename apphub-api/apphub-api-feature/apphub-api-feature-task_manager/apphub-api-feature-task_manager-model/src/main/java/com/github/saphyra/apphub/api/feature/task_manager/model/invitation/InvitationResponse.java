package com.github.saphyra.apphub.api.feature.task_manager.model.invitation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InvitationResponse {
    private UUID invitedByUserId;
    private String invitedByUsername;
    private String invitedByUserEmail;
    private UUID organizationId;
    private String organizationName;
    private String organizationDescription;
}
