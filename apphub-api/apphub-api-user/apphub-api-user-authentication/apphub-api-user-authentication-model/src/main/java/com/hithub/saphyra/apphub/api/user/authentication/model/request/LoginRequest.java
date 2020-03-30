package com.hithub.saphyra.apphub.api.user.authentication.model.request;

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
    private String email;
    private String password;
    private Boolean rememberMe;
}
