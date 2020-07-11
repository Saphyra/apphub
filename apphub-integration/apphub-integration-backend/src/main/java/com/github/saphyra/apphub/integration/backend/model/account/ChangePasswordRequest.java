package com.github.saphyra.apphub.integration.backend.model.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ChangePasswordRequest {
    private final String newPassword;
    private final String password;
}
