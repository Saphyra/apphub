package com.github.saphyra.apphub.integration.structure.api.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String userIdentifier;
    private String password;
    private Boolean rememberMe;
}
