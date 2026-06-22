package com.github.saphyra.apphub.api.feature.task_manager.model.organization;

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
