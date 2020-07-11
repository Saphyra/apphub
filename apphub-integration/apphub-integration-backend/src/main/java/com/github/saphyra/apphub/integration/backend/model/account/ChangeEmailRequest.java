package com.github.saphyra.apphub.integration.backend.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ChangeEmailRequest {
    private String email;
    private String password;
}
