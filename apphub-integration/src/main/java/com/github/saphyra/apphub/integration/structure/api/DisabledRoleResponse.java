package com.github.saphyra.apphub.integration.structure.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DisabledRoleResponse {
    private String role;
    private boolean disabled;
}
