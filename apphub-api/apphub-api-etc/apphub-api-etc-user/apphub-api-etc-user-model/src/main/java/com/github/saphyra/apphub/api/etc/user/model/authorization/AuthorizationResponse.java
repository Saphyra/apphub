package com.github.saphyra.apphub.api.etc.user.model.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AuthorizationResponse {
    private UUID userId;
    private List<String> roles;
}
