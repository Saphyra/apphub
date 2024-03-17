package com.github.saphyra.apphub.integration.structure.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RoleRequest {
    private UUID userId;
    private String role;
    private String password;
}
