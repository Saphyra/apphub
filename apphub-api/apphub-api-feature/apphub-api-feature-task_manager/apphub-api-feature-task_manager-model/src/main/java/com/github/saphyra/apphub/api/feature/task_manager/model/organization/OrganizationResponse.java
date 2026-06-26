package com.github.saphyra.apphub.api.feature.task_manager.model.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrganizationResponse {
    private UUID  organizationId;
    private String organizationName;
    private String description;
}
