package com.github.saphyra.apphub.integration.frontend.model.account.change_username;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ChangeUsernameValidationResult {
    private final UsernameValidationResult username;
    private final ChUsernamePasswordValidationResult password;

    public boolean allValid() {
        return username == UsernameValidationResult.VALID
            && password == ChUsernamePasswordValidationResult.VALID;
    }
}
