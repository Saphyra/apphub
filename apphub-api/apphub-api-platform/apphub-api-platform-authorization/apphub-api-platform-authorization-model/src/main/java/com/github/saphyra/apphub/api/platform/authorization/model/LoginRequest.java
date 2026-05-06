package com.github.saphyra.apphub.api.platform.authorization.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class LoginRequest {
    private String userIdentifier; //Username or email
    private String password;
    private Boolean rememberMe;
}
