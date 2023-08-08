package com.github.saphyra.apphub.integration.structure.api.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRoleResponse {
    private UUID userId;
    private String email;
    private String username;
    private List<String> roles;
}
