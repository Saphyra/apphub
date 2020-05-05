package com.github.saphyra.apphub.integration.frontend.model.registration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class RegistrationValidationResult {
    private final EmailValidationResult email;
    private final UsernameValidationResult username;
    private final PasswordValidationResult password;
    private final PasswordValidationResult confirmPassword;

    public boolean allValid() {
        return
            email == EmailValidationResult.VALID
                && username == UsernameValidationResult.VALID
                && password == PasswordValidationResult.VALID
                && confirmPassword == PasswordValidationResult.VALID;
    }
}
