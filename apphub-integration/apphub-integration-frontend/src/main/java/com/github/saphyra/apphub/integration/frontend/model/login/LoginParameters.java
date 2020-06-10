package com.github.saphyra.apphub.integration.frontend.model.login;

import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginParameters {
    @Builder.Default
    private String email = "";

    @Builder.Default
    private String password = "";

    public static LoginParameters fromRegistrationParameters(RegistrationParameters userData) {
        return builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .build();
    }
}
