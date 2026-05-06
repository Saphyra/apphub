package com.github.saphyra.apphub.api.etc.user.model.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
public class AuthorizationRequest {
    private String userIdentifier; //Username or e-mail
    private String password;
}
