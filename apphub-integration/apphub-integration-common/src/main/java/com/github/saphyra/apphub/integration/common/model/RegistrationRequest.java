package com.github.saphyra.apphub.integration.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    private String username;
    private String email;
    private String password;
}
