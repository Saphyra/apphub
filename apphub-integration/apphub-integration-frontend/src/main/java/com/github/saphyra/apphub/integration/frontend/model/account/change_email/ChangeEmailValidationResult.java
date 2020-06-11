package com.github.saphyra.apphub.integration.frontend.model.account.change_email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ChangeEmailValidationResult {
    private final EmailValidationResult email;
    private final ChEmailPasswordValidationResult password;

    public boolean allValid() {
        return email == EmailValidationResult.VALID
            && password == ChEmailPasswordValidationResult.VALID;
    }
}
