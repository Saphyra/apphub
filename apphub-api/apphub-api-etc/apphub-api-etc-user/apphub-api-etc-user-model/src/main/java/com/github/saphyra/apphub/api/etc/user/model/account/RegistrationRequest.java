package com.github.saphyra.apphub.api.etc.user.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class RegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String language;
}
