package com.github.saphyra.apphub.integration.structure.api;

import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
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
    private String userIdentifier = "";

    @Builder.Default
    private String password = "";

    public static LoginParameters fromRegistrationParameters(RegistrationParameters userData) {
        return builder()
            .userIdentifier(userData.getEmail())
            .password(userData.getPassword())
            .build();
    }
}
