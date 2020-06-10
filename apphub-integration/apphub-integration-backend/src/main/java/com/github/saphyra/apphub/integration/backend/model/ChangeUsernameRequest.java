package com.github.saphyra.apphub.integration.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ChangeUsernameRequest {
    private String username;
    private String password;
}
