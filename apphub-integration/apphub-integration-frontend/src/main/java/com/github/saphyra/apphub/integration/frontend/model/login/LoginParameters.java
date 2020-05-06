package com.github.saphyra.apphub.integration.frontend.model.login;

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
}
