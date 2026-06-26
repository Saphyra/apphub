package com.github.saphyra.apphub.integration.structure.api.task_manager.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class CreateOrganizationRequest {
    private String organizationName;
    private String description;
    private List<UUID> invitedUsers;
}
