package com.github.saphyra.apphub.integration.structure.user.change_password;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ChangePasswordValidationResult {
    private final NewPasswordValidationResult newPassword;
    private final ConfirmPasswordValidationResult confirmPassword;
    private final ChPasswordPasswordValidationResult password;

    public boolean allValid() {
        return newPassword == NewPasswordValidationResult.VALID
            && confirmPassword == ConfirmPasswordValidationResult.VALID
            && password == ChPasswordPasswordValidationResult.VALID;
    }
}
